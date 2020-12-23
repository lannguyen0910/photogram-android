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

STORAGE_PATH = 'rest/files/'
IMG_DIR = 'images'
DEFAULT_DIR = 'default'
USER_DEFAULT_AVATAR = 'avatar.jpg'

class MyMobileView():
    def __init__(self):
        self.username = None
        self.current_user_id = -1
        self.current_pic_id = 0
        
    def checkIsLoggedIn(self):
        return self.current_user_id > 0
         
    def setCurrentUserID(self, request):
        self.current_user_id = request.user.id

    def setCurrentImageID(self):
        self.current_pic_id = self.getAvailableImageID()

    def getCurrentUserImageDir(self):
        return os.path.join(STORAGE_PATH, str(self.current_user_id), IMG_DIR)

    def getAvailableImageID(self):
        result_id = 0
        user_imgdir = self.getCurrentUserImageDir()
        storage  = sorted(os.listdir(user_imgdir))
        for path in storage:
            img_id,  ext = path.split('.')
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
        return info_dict

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

            # Test style transfering
            # new_filename = f'{self.current_pic_id}.jpg'
            # new_filepath = os.path.join(user_imgdir, new_filename)
            # self.current_pic_id+=1
            # getStyleTransfer(filepath, 'rest/editors/style_transfer/examples/style/in00.png', new_filepath)
        except Exception as e:
            print(e)

    def convertImagetoString(self, filename):
        with open(filename, 'rb') as image_file:
            encoded_string = base64.b64encode(image_file.read()).decode()
        return encoded_string

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

    @csrf_exempt
    def sendAllImagesToUser(self, request):
        response_data = {}
        response_data['images'] = []
        response_data['message'] = "Sent images"
        response_data['response'] = 1
        if request.method == 'POST':
            user_image_paths = self.getAllImagesByUserID()
            print(user_image_paths)
            for path in user_image_paths['images']:
                img_string = self.convertImagetoString(path)
                response_data['images'].append(img_string)
            img_string = self.convertImagetoString(user_image_paths['avatar'])
            response_data['avatar'] = img_string
        return HttpResponse(json.dumps(response_data), content_type="application/json")

    @csrf_exempt
    def uploadImage(self, request):
        response_data = {}
        if self.checkIsLoggedIn():
            if request.method == 'POST':
                self.handleUploadFile(request)
            else:
                response_data['error'] = 'method not supported'
        else:
            response_data['error'] = 'Not logged in'
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

