from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse
import json
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login, logout

from PIL import Image
import io
import os
import base64

from .forms import logInForm, signUpForm


STORAGE_PATH = 'rest/files/'

class MyWebView():
    def __init__(self):
        self.username = None
        self.current_user_id = 0
        self.current_pic_id = 0

    def checkIsLoggedIn(self):
        return self.current_user_id > 0
         
    def setCurrentUserID(self, request):
        self.current_user_id = request.user.id

    def setCurrentImageID(self):
        self.current_pic_id = self.getAvailableImageID()

    def getAvailableImageID(self):
        result_id = 0
        storage  = sorted(os.listdir(STORAGE_PATH))
        for path in storage:
            filename,  ext = path.split('.')
            usr_id, img_id = filename.split('_')
            if int(usr_id) == user_id:
                if result_id == int(img_id):
                    result_id += 1
        return result_id

    def handleUploadFile(self, request):
        try:
            img_string = request.POST['image']
            imgdata = base64.b64decode(img_string)
            filename = f'{self.current_user_id}_{self.current_pic_id}.jpg'
            filepath = os.path.join(STORAGE_PATH, filename)
            while os.path.isfile(filepath):
                self.setCurrentImageID()
                filename = f'{user_id}_{pic_id}.jpg'
                filepath = os.path.join(STORAGE_PATH, filename)
                
            with open(filepath, 'wb') as f:
                f.write(imgdata)
                self.current_pic_id += 1
                print(f"Image is saved at {filepath}")
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

    @csrf_exempt
    def sendAllImagesToUser(self, request):
        response_data = {}
        response_data['images'] = []
        response_data['message'] = "Sent images"
        if request.method == 'POST':
            user_image_paths = self.getAllImagesByUserID()
            for path in user_image_paths:
                img_string = self.convertImagetoString(path)
                response_data['images'].append(img_string)
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
           return redirect('home')
        else:
            if request.method == 'POST':
                user_form = logInForm(data = request.POST)
                if user_form.is_valid():
                    user = user_form.login(request)
                    self.setCurrentUserID(request)
                    if user is not None:
                        print(f'{user}_{self.current_user_id} has logged in')
                        self.username = user
                        return redirect('home')
            user_form = logInForm()
        return render(request, 'registration/login.html',{'form': user_form})

    @csrf_exempt
    def signUpUser(self, request):
        response_data = {}
        if self.checkIsLoggedIn():
           return redirect('home')
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
            else:
                form = signUpForm()
        return render(request, 'registration/signup.html',{'form': form})

    def resetUser(self):
        self.current_pic_id = 0
        self.current_user_id = 0
        self.username = None

    @csrf_exempt
    def logOutUser(self, request):
        response_data = {}
        try:
            logout(request)
            print(f'{self.username}_{self.current_user_id} has logged out')
            self.resetUser()
        except:
            pass
        return redirect('home')
            
        


