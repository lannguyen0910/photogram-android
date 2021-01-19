import pickle
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from googleapiclient.http import MediaFileUpload

# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/drive']
DEFAULT_TOKEN_PICKLE_NAME = './rest/gdrive/token.pickle'
DEFAULT_CREDENTIAL_FILE = './rest/gdrive/credentials.json'
DEFAULT_ROOT_FOLDER_ID = '147JudIeJfZiGpzfS0UDT-Z3ySRyVUxZT'
DEFAULT_ROOT_FOLDER_NAME = 'storage'

class GoogleDriveUploader():
    def __init__(self):
        self.folder_name_to_id = {}
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
        return self.folder_name_to_id[folder_name]

    def getFileIDInsideParentFolder(self, folder_name, filename):
        folderid = self.getFolderIDByName(folder_name)
        query = f"mimeType='image/jpeg' and \
                '{folderid}' in parents and \
                trashed = false"
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
        file_metadata = {'name': photo_path, "parents": [dest_folder_id]}
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

if __name__ == '__main__':
    gdrive_uploader = GoogleDriveUploader()
    gdrive_uploader.deleteFileFromDrive('storage/2/images/0.jpg')
