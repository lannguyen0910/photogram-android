from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class MyUser(User):
    phone_number = models.CharField(max_length=30, default="")
    name = models.CharField(max_length=30, default="")

    def get_phone_number(self):
        return self.phone_number

    def get_full_name(self):
        return self.name


