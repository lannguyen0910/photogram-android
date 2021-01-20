import pickle
import io
import os.path
import shutil
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from googleapiclient.http import MediaFileUpload, MediaIoBaseDownload

# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/drive']
DEFAULT_TOKEN_PICKLE_NAME = './rest/gdrive/token.pickle'
DEFAULT_CREDENTIAL_FILE =  './rest/gdrive/credentials.json'
DEFAULT_ROOT_FOLDER_ID = '1nFozcz0cfqqLM7mHvaxE-auGwiFqDpkQ'
DEFAULT_ROOT_FOLDER_NAME = 'rest/files'

class GoogleDriveUploader():
    def __init__(self):
        self.folder_name_to_id = {}
        self.folder_name_to_id[DEFAULT_ROOT_FOLDER_NAME] = DEFAULT_ROOT_FOLDER_ID
        self.creds = None
        if os.path.exists(DEFAULT_TOKEN_PICKLE_NAME):
            with open(DEFAULT_TOKEN_PICKLE_NAME, 'rb') as token:
                self.creds = pickle.load(token)
        # If there are no (valid) credentials available, let the user log in.
        if not self.creds or not self.creds.valid:
            if self.creds and self.creds.expired and self.creds.refresh_token:
                self.creds.refresh(Request())
            else:
                flow = InstalledAppFlow.from_client_secrets_file(DEFAULT_CREDENTIAL_FILE, SCOPES)
                self.creds = flow.run_local_server(port=0)
      
            with open(DEFAULT_TOKEN_PICKLE_NAME, 'wb') as token:
                pickle.dump(self.creds, token)
        
        self.service = build('drive', 'v3', credentials=self.creds)

        print('Recursive matching folder\'s name and id')
        self.getFolderIDList(DEFAULT_ROOT_FOLDER_ID, DEFAULT_ROOT_FOLDER_NAME)

    def getFolderIDList(self, parent_id, parent_name):
        query = f"mimeType='application/vnd.google-apps.folder' and \
                '{parent_id}' in parents and \
                trashed = false"
        page_token = None
        while True:
            response = self.service.files().list(q=query,
                                                spaces='drive',
                                                fields='nextPageToken, files(id, name)',
                                                pageToken=page_token).execute()
            for files in response.get('files', []):
                # Process change
                filename = '/'.join([parent_name, files.get('name')])
                fileid = files.get('id')
                print('Path: ', filename, '\t\tID: ', fileid)
                self.folder_name_to_id[filename] = fileid
                self.getFolderIDList(fileid, filename)
            page_token = response.get('nextPageToken', None)
            if page_token is None:
                break
    
    def createFolder(self, parent_name, folder_name):
        parent_id = self.getFolderIDByName(parent_name)
        file_metadata = {
            'name': folder_name,
            'parents': [parent_id],
            'mimeType': 'application/vnd.google-apps.folder'
        }

        new_folder_name = '/'.join([parent_name, folder_name])
        if new_folder_name in self.folder_name_to_id:
            return self.folder_name_to_id[new_folder_name]

        files = self.service.files().create(body=file_metadata,
                                            fields='id').execute()

        new_folder_id = files.get('id')
        self.folder_name_to_id[new_folder_name] = new_folder_id                  
        print(f"Folder created. Path: {new_folder_name} \t\tID: {new_folder_id}")
    
    def getFolderIDByName(self, folder_name):
        folder_name = folder_name.replace('\\\\', '/')
        print(folder_name)
        return self.folder_name_to_id[folder_name]

    def getFileIDInsideParentFolder(self, folder_name, filename):
        folderid = self.getFolderIDByName(folder_name)
    
        query = f"mimeType!='application/vnd.google-apps.folder' and \
                '{folderid}' in parents"
        page_token = None
        while True:
            response = self.service.files().list(q=query,
                                                spaces='drive',
                                                fields='nextPageToken, files(id, name)',
                                                pageToken=page_token).execute()
            for files in response.get('files', []):
                if files.get('name') == filename:
                    return files.get('id')
                
            page_token = response.get('nextPageToken', None)
            if page_token is None:
                return None
        return None


    def uploadFileToDrive(self, photo_path, dest_path):
        dest_folder_id = self.getFolderIDByName(dest_path)
        file_metadata = {'name': os.path.basename(photo_path), "parents": [dest_folder_id]}
        media = MediaFileUpload(photo_path, mimetype='image/jpeg')
        files = self.service.files().create(body=file_metadata,
                                            media_body=media,
                                            fields='id').execute()
        print(f'File uploaded to {dest_path}')

    def deleteFileFromDrive(self, filename):
        parent_folder, basename = os.path.split(filename)
        fileid = self.getFileIDInsideParentFolder(parent_folder, basename)
        files = self.service.files().delete(fileId=fileid).execute()
        print(f"Delete {filename}")

    def copyFile(self, ori_path, new_path):
        parentname, filename = os.path.split(ori_path)
        parentid = self.getFolderIDByName(new_path)
        fileid = self.getFileIDInsideParentFolder(parentname, filename)
        newfile = {'name': filename, 'parents' : [parentid]}
        files = self.service.files().copy(fileId=fileid, body=newfile).execute()

    def downloadAllFolders(self, parent_folder):
        if not os.path.exists(parent_folder):
            os.makedirs(parent_folder)
        folderid = self.getFolderIDByName(parent_folder)
        query = f"mimeType='application/vnd.google-apps.folder' and \
                '{folderid}' in parents and \
                trashed = false"
        page_token = None
        while True:
            response = self.service.files().list(q=query,
                                                spaces='drive',
                                                fields='nextPageToken, files(id, name)',
                                                pageToken=page_token).execute()
            for files in response.get('files', []):
                folder_name = '/'.join([parent_folder, files.get('name')])
                self.downloadAllFolders(folder_name)
                self.downloadAllFilesFromFolder(folder_name, folder_name)
            page_token = response.get('nextPageToken', None)
            if page_token is None:
                break

    def downloadAllFilesFromFolder(self, folder_path, folder_out):
        folderid = self.getFolderIDByName(folder_path)
        query = f"mimeType!='application/vnd.google-apps.folder' and \
                '{folderid}' in parents and \
                trashed = false"
        page_token = None
        while True:
            response = self.service.files().list(q=query,
                                                spaces='drive',
                                                fields='nextPageToken, files(id, name)',
                                                pageToken=page_token).execute()
            for files in response.get('files', []):
                fileout = os.path.join(folder_out, files.get('name'))
                print(f"Downloading to {fileout}", end='. ')
                self.downloadSingleFileByID(files.get('id'), fileout)
            page_token = response.get('nextPageToken', None)
            if page_token is None:
                break
    
    def downloadSingleFileByID(self, fileid, fileout):
        request = self.service.files().get_media(fileId=fileid)
        fh = io.BytesIO()
        downloader = MediaIoBaseDownload(fh, request)
        done = False
        while done is False:
            status, done = downloader.next_chunk()
            print("Download %d%%." % int(status.progress() * 100))
        fh.seek(0)
        with open(fileout, 'wb') as f: 
            shutil.copyfileobj(fh, f)

if __name__ == '__main__':
    DEFAULT_TOKEN_PICKLE_NAME = 'token.pickle'
    DEFAULT_CREDENTIAL_FILE =  'credentials.json'
    gdrive_uploader = GoogleDriveUploader()

    s = gdrive_uploader.getFileIDInsideParentFolder('rest/files/3', 'fav.txt')
    print(s)