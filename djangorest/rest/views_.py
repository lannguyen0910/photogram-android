from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse
import json
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login, logout
from .models import MyUser

from PIL import Image
import io
import os
import base64
from shutil import copyfile

from .forms import logInForm, signUpForm
from .editor import *
from .gdrive import *
from google_drive_downloader import GoogleDriveDownloader as gdd

STORAGE_PATH = 'rest/files'
IMG_DIR = 'images'
DEFAULT_DIR = 'default'
USER_DEFAULT_AVATAR = 'avatar.jpg'
USER_DEFAULT_FAV = 'fav.txt'
TEMPORARY_DIR = 'temp'


class MyMobileView():
    def __init__(self):
        self.username = None
        self.current_user_id = -1
        self.current_pic_id = 0
        self.gdrive_uploader = GoogleDriveUploader()
        self.initServer()

    # Google Drive interfere:

    def initServer(self):
        if not os.path.exists(STORAGE_PATH):
            self.gdrive_uploader.downloadAllFolders(STORAGE_PATH)

    def initUserData(self):
        self.gdrive_uploader.createFolder(DEFAULT_ROOT_FOLDER_NAME, str(self.current_user_id))
        usr_dir = '/'.join([DEFAULT_ROOT_FOLDER_NAME, str(self.current_user_id)])
        self.gdrive_uploader.createFolder(usr_dir, 'images')
        default_avatar = os.path.join(STORAGE_PATH, DEFAULT_DIR, USER_DEFAULT_AVATAR)
        self.gdrive_uploader.copyFile(default_avatar, self.getCurrentUserImageDir())
        
    def loadUserData(self):
        usr_dir = '/'.join([DEFAULT_ROOT_FOLDER_NAME, str(self.current_user_id)])
        if not os.path.exists(usr_dir):
            self.downloadUserDataFromDrive(usr_dir)

    def downloadUserDataFromDrive(self, folder_name):
        print('Start downloading user data')
        self.gdrive_uploader.downloadAllFolders(folder_name)
        print('User data downloaded')

    # Server interfere:

    def checkIsLoggedIn(self):
        return self.current_user_id > 0
         
    def setCurrentUserID(self, request):
        self.current_user_id = request.user.id

    def setCurrentImageID(self):
        self.current_pic_id = self.getAvailableImageID()

    def getCurrentUserImageDir(self):
        return os.path.join(STORAGE_PATH, str(self.current_user_id), IMG_DIR)

    def getCurrentUserDir(self):
        return os.path.join(STORAGE_PATH, str(self.current_user_id))

    def getAvailableImageID(self):
        result_id = 0
        user_imgdir = self.getCurrentUserImageDir()
        storage  = sorted(os.listdir(user_imgdir))
        for path in storage:
            img_id,  ext = path.split('.')
            if img_id == 'avatar':
                continue
            if result_id == int(img_id):
                result_id += 1
        return result_id

    def getUserAvatar(self):
        return os.path.join(STORAGE_PATH, str(self.current_user_id), IMG_DIR, USER_DEFAULT_AVATAR)

    def getUserInfoResponse(self):
        info_dict = {}
        user_info = MyUser.objects.get(id=self.current_user_id)
        info_dict['username'] = user_info.username
        info_dict['email'] = user_info.email
        info_dict['password'] = user_info.password
        info_dict['fullname'] = user_info.name
        info_dict['phone_number'] = user_info.phone_number
        info_dict['avatar'] = self.convertImagetoString(self.getUserAvatar())
        return info_dict

    def deleteImageByID(self, idx):
        user_imgdir = self.getCurrentUserImageDir()
        result_code = 0
        img_path = os.path.join(user_imgdir, f"{idx}.jpg")
        if os.path.isfile(img_path):
            os.remove(img_path)
            print(f"Remove {img_path} from {self.current_user_id} database")
            result_code = 1
        return result_code
    
    
    def handleDeleteFile(self, request):
        flag = 0
        try:
            img_id = request.POST['image']
            user_imgdir = self.getCurrentUserImageDir()
            img_path = os.path.join(user_imgdir, f'{img_id}.jpg')
            if os.path.isfile(img_path):
                os.remove(img_path)
                print(f"Remove image {img_path}")
                flag = 1
                self.gdrive_uploader.deleteFileFromDrive(img_path)
            return flag
        except Exception as e:
            print(e)
            return flag
    

    def changeUserAvatar(self, image_string):
        user_imgdir = self.getUserAvatar()
        image = self.convertStringToImage(image_string)
        with open(user_imgdir, "wb") as f:
            f.write(image)
            self.gdrive_uploader.uploadFileToDrive(user_imgdir, os.path.dirname(user_imgdir))

    @csrf_exempt
    def updateProfile(self, request):
        response_data = {}
        response_data['message'] = "Failed to update"
        response_data['response'] = 0
        if request.method == 'POST':
            user = MyUser.objects.filter(pk=self.current_user_id).first()
            form = signUpForm(request.POST, instance=user)
            if form.is_valid():
                form.save()
                if "avatar" in request.POST.keys():
                    print("yes")
                    self.changeUserAvatar(request.POST['avatar'])
                response_data['message'] = "Success to update"
                response_data['response'] = 1
                print("Update form valid")
            else:
                print("Update form invalid")
        else:
            response_data['message'] = "Method not support"
            response_data['response'] = 0
            print("Update form invalid")
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def deleteImage(self, request):
        response_data = {}
        response_data['images'] = []
        response_data['message'] = "Failed to delete image"
        response_data['response'] = 0
        if request.method == 'POST':
            result = self.handleDeleteFile(request)
            if result:
                response_data['message'] = "Deleted image"
                response_data['response'] = 1
            else:
                response_data['message'] = "Image not found"
                response_data['response'] = 0
        else:
            response_data['message'] = "Method not support"
            response_data['response'] = 0
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    def handleUploadFile(self, request):
        try:
            img_string = request.POST['image']
            imgdata = base64.b64decode(img_string)
            filename = f'{self.current_pic_id}.jpg'
            user_imgdir = self.getCurrentUserImageDir()
            filepath = os.path.join(user_imgdir, filename)
            while os.path.isfile(filepath):
                self.setCurrentImageID()
                filename = f'{self.current_pic_id}.jpg'
                filepath = os.path.join(user_imgdir, filename)
                
            with open(filepath, 'wb') as f:
                f.write(imgdata)
                self.current_pic_id += 1
                print(f"Image is saved at {filepath}")

            self.gdrive_uploader.uploadFileToDrive(filepath, user_imgdir)
        except Exception as e:
            print(e)

    def convertImagetoString(self, filename):
        with open(filename, 'rb') as image_file:
            encoded_string = base64.b64encode(image_file.read()).decode()
        return encoded_string
    
    def convertStringToImage(self, image_string):
        decoded_image = base64.b64decode(str(image_string))
        return decoded_image

    def sendImageToClient(self, filename):
        response_data = {}
        img_string = self.convertImagetoString(filename)
        response_data['image'] = img_string
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    def getAllImagesByUserID(self):
        user_imgdir = self.getCurrentUserImageDir()
        image_paths = sorted(os.listdir(user_imgdir))
        user_image_paths = [os.path.join(user_imgdir, image_name) for image_name in image_paths if image_name != 'avatar.jpg']
        user_avatar = os.path.join(user_imgdir, USER_DEFAULT_AVATAR)
        return {'images': user_image_paths, 'avatar': user_avatar}

    def setupNewUser(self):
        new_user_imgdir = os.path.join(STORAGE_PATH, str(self.current_user_id), IMG_DIR)
        if not os.path.exists(new_user_imgdir):
            os.makedirs(new_user_imgdir)

        default_avatar = os.path.join(STORAGE_PATH, DEFAULT_DIR, USER_DEFAULT_AVATAR)
        copy_avatar = os.path.join(new_user_imgdir, USER_DEFAULT_AVATAR)
        copyfile(default_avatar, copy_avatar)

    def getImageIDByName(self, image_name):
        name = os.path.basename(image_name)
        name,_ = name.split('.')
        return name
    
    @csrf_exempt
    def uploadStyleTransferImage(self, request):
        response_data = {}
        response_data['response'] = 0
        response_data['message'] = "Failed to upload"
        if self.checkIsLoggedIn():
            if request.method == 'POST':
                print("yes1")
                self.styleTransferImage(request)
                response_data['response'] = 1
                response_data['message'] = "Transfer success"
            else:
                response_data['response'] = 0
                response_data['message'] = "Method not supported"
        else:
            response_data['response'] = 0
            response_data['message'] = "Not logged in"
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    def styleTransferImage(self, request):
        try:
            img_string = request.POST['image']
            imgdata = base64.b64decode(img_string)
            filename = f'{self.current_user_id}_{self.current_pic_id}.jpg'
            user_imgdir = self.getCurrentUserImageDir()
            filepath = os.path.join(STORAGE_PATH, TEMPORARY_DIR, filename)
            new_filename = f'{self.current_pic_id}.jpg'
            new_filepath = os.path.join(user_imgdir, new_filename)

            while os.path.isfile(new_filepath):
                self.setCurrentImageID()
                new_filename = f'{self.current_pic_id}.jpg'
                new_filepath = os.path.join(user_imgdir, new_filename)
            with open(filepath, 'wb') as f:
                f.write(imgdata)
                print(f"Temp image is saved at {filepath}")
                self.current_pic_id+=1
            getStyleTransfer(filepath, 'rest/editors/style_transfer/examples/style/in14.png', new_filepath)
        except Exception as e:
            print(e)

        

    @csrf_exempt
    def sendAllImagesToUser(self, request):
        response_data = {}
        response_data['images'] = []
        response_data['image_names'] = []
        response_data['message'] = "Sent images"
        response_data['response'] = 1
        if request.method == 'POST':
            user_image_paths = self.getAllImagesByUserID()
            print(user_image_paths)
            for path in user_image_paths['images']:
                img_string = self.convertImagetoString(path)
                img_name = self.getImageIDByName(path)
                response_data['images'].append(img_string)
                response_data['image_names'].append(img_name)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def uploadImage(self, request):
        response_data = {}
        response_data['response'] = 0
        response_data['message'] = "Failed to upload"
        if self.checkIsLoggedIn():
            if request.method == 'POST':
                self.handleUploadFile(request)
                response_data['response'] = 1
                response_data['message'] = "Upload success"
            else:
                response_data['response'] = 0
                response_data['message'] = "Method not supported"
        else:
            response_data['response'] = 0
            response_data['message'] = "Not logged in"
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def logInUser(self, request):
        response_data = {}
        if self.checkIsLoggedIn():
           response_data['response'] = 1
           response_data['message'] = "Already logged in"
           response_data['user_info'] = self.getUserInfoResponse()
        else:
            if request.method == 'POST':
                user_form = logInForm(data = request.POST)
                if user_form.is_valid():
                    user = user_form.login(request)
                    if user is not None:
                        self.setCurrentUserID(request)
                        self.loadUserData()
                        response_data['user_info'] = self.getUserInfoResponse()
                        response_data['response'] = 1
                        response_data['message'] = "Log in successfully"
                        print(f'{user}_{self.current_user_id} has logged in')
                        self.username = user
                    else:
                        response_data['response'] = 0
                        response_data['message'] = 'Failed to login'
                else:
                    response_data['response'] = 0
                    response_data['message'] = 'Wrong format'
            else:
                response_data['response'] = 0
                response_data['message'] = f'{request.method} not supported'
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def signUpUser(self, request):
        response_data = {}
        if self.checkIsLoggedIn():
           response_data['response'] = 1
           response_data['message'] = "Already logged in"
           response_data['user_info'] = self.getUserInfoResponse()
        else:
            if request.method == 'POST':
                form = signUpForm(request.POST)
                if form.is_valid():
                    form.save()
                    username = form.cleaned_data.get('username')
                    raw_password = form.cleaned_data.get('password1')
                    user = authenticate(username=username, password=raw_password)
                    login(request, user)
                    self.setCurrentUserID(request)
                    self.setupNewUser()
                    response_data['user_info'] = self.getUserInfoResponse()
                    print(f'{user}_{self.current_user_id} has signed up')
                    response_data['response'] = 1
                    response_data['message'] = "Sign up successfully"
                    self.initUserData()
                else:
                    response_data['response'] = 0
                    response_data['message'] = "Wrong format"
            else:
                response_data['response'] = 0
                response_data['message'] = f'{request.method} not supported'
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    def resetUser(self):
        self.current_pic_id = 0
        self.current_user_id = -1
        self.username = None

    @csrf_exempt
    def logOutUser(self, request):
        response_data = {}
        response_data['response'] = 0
        response_data['message'] = 'Log out failed'
        if request.method == 'POST':
            logout(request)
            print(f'{self.username}_{self.current_user_id} has logged out')
            self.resetUser()
            response_data['response'] = 1
            response_data['message'] = 'Log out success'
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    def setFavorite(self, image_id):
        usr_dir = self.getCurrentUserDir()
        usr_fav_txt = os.path.join(usr_dir, USER_DEFAULT_FAV)
        fileread = open(usr_fav_txt, "r+")
        lines = fileread.readlines()
        fileread.close()

        flag = 0
        for idx, line in enumerate(lines):
            if str(line) == image_id:
                del lines[idx]
                flag = 1
                break

        fileappend = open(usr_fav_txt, "w")
        for line in lines:
            fileappend.write(line)
            fileappend.write('\n')

        if not flag:
            fileappend.write(str(image_id))

        fileappend.close()
        return flag

    def getImgFavoriteStatus(self, image_id):
        usr_dir = self.getCurrentUserDir()
        usr_fav_txt = os.path.join(usr_dir, USER_DEFAULT_FAV)
        fileread = open(usr_fav_txt, "r+")
        lines = fileread.readlines()
        fileread.close()

        flag = 0
        for idx, line in enumerate(lines):
            if str(line) == image_id:
                flag = 1
                break

        return flag

    @csrf_exempt
    def handleFavoriteStatus(self, request):
        response_data = {}
        response_data['response'] = 0
        response_data['message'] = 'Set/Get Favorite failed'
        if request.method == 'POST':
            img_id = request.POST['image']
            mode = request.POST["mode"]
            if mode == "0":
                flag = self.setFavorite(img_id)
                response_data['response'] = 1
            else:
                flag = self.getImgFavoriteStatus(img_id)
                response_data['response'] = flag   # 1 is found, 0 is not found  
            response_data['message'] = 'Set/Get Favorite successfully' if flag else 'Set/Get Unfavorite successfully'
        return HttpResponse(json.dumps(response_data), content_type="application/json")
            

    
    def getAllFavoriteImgs(self):
        usr_dir = self.getCurrentUserDir()
        usr_img_dir = self.getAllImagesByUserID()
        usr_fav_txt = os.path.join(usr_dir, USER_DEFAULT_FAV)

        with open(usr_fav_txt, 'r') as f:
            data = f.read()
            for row in data.splitlines():
                path = os.path.join(usr_img_dir, row+'.jpg')
                print(path)
                img_string = self.convertImagetoString(path)
                img_name = self.getImageIDByName(path)
                response_data['images'].append(img_string)
                response_data['image_names'].append(img_name)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def sendAllFavoriteImagesToUser(self, request):
        response_data = {}
        response_data['images'] = []
        response_data['image_names'] = []
        response_data['message'] = "Sent images"
        response_data['response'] = 1
        if request.method == 'POST':
            user_image_paths = self.getAllFavoriteImgs()
            print(user_image_paths)
            for path in user_image_paths['images']:
                img_string = self.convertImagetoString(path)
                img_name = self.getImageIDByName(path)
                response_data['images'].append(img_string)
                response_data['image_names'].append(img_name)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
