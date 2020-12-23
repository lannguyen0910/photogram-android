from django import forms
from django.contrib.auth import authenticate, login
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import MyUser

class logInForm(forms.Form):

    user = forms.CharField(label = 'Username ', max_length = 25)
    pwd  = forms.CharField(label = 'Password ', widget =  forms.PasswordInput)

    def __init__(self, *args, **kwargs):
        self.user_cache = None # You need to make the user as None initially
        super(forms.Form, self).__init__(*args, **kwargs)

    def clean(self):

        cleaned_data = super(logInForm, self).clean()
        user         = cleaned_data.get("user")
        pwd          = cleaned_data.get("pwd")

        # Now you get the user
        self.user_cache = authenticate(username=user, password=pwd)
        # Do other stuff
        return self.cleaned_data

    # Function to return user in views
    def get_user(self):
        return self.user_cache

    def login(self, request):
        self.clean()
        if self.user_cache is not None:
            login(request, self.user_cache)
            return self.get_user()
        return None
            


class signUpForm(UserCreationForm):
    name = forms.CharField(max_length=30, required=False, help_text='Optional.')
    password1 = forms.CharField(
        label="Password",
        strip=False,
        widget=forms.PasswordInput(),
    )
    email = forms.EmailField(max_length=254, help_text='Required. Inform a valid email address.')
    phone_number = forms.CharField(max_length=30, required=False, help_text='Optional.')
    
    class Meta:
        model = MyUser
        fields = ('username', 'password1', 'name' ,'email', 'phone_number')


    def login(self, request):
        username = self.cleaned_data.get('username')
        raw_password = self.cleaned_data.get('password1')
        user = authenticate(username=username, password=raw_password)
        login(request, user)


# class updateProfileForm(UserCreationForm):
#     # name = forms.CharField(max_length=30, required=False, help_text='Optional.')
#     # password1 = forms.CharField(
#     #     label="Password",
#     #     strip=False,
#     #     widget=forms.PasswordInput(),
#     # )
#     email = forms.EmailField(max_length=254, help_text='Required. Inform a valid email address.')
#     phone_number = forms.CharField(max_length=30, required=False, help_text='Optional.')
    
#     class Meta:
#         model = User
#         fields = ('username', 'password1', 'name' ,'email', 'phone_number')


#     def login(self, request):
#         username = self.cleaned_data.get('username')
#         raw_password = self.cleaned_data.get('password1')
#         user = authenticate(username=username, password=raw_password)
#         login(request, user)