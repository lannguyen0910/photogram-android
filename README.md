# Mobile-Final

## Requirements:
- Pytorch 1.6
- Python

## Install these first:
- pip install --upgrade google-api-python-client google-auth-httplib2 google-auth-oauthlib
- pip install Django
- pip install djangorestframework
- pip install markdown
- pip install pdjango-filter


## Find current local IP address
- Use ```ipconfig``` to find local IP address (ex: 192.168.1.2)
- Change all IPs in Android app's string resoures

## To start Django server:
- Go to ```djangorest```
- Create superuser ```python manage.py createsuperuser```, fill in admin's username and password
- Run ```python manage.py makemigrations```
- Run ```python manage.py migrate```
- Run ```python manage.py runserver 192.168.1.2:8000```
