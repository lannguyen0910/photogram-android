"""djangorest URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.contrib import admin
from django.urls import path
from django.conf.urls import url, include
from django.views.generic.base import TemplateView

from rest_framework import routers
from rest.views import MyWebView
from rest.views_ import MyMobileView


mobile_view = MyMobileView()
# web_view = MyWebView()


urlpatterns = [
    path('admin/', admin.site.urls),
    path('', TemplateView.as_view(template_name='home.html'), name='home'),
    url('upload/', mobile_view.uploadImage, name='upload'),
    url('login/', mobile_view.logInUser, name='login'),
    url('signup/', mobile_view.signUpUser, name='signup'),
    url('logout/', mobile_view.logOutUser, name='logout'),
    url('gallery/', mobile_view.sendAllImagesToUser, name='gallery'),
    url('delete/', mobile_view.deleteImage, name='delete'),
    url('update/', mobile_view.updateProfile, name='update'),
    url('style/', mobile_view.uploadStyleTransferImage, name='style'),
    url('fav/', mobile_view.sendAllFavoriteImagesToUser, name='fav'),
    url('fav_status/', mobile_view.handleFavoriteStatus, name='fav_status'),
]
