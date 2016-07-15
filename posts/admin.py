from django.contrib import admin

# Register your models here.
from .models import UserProfile, JobPost

admin.site.register(UserProfile)
admin.site.register(JobPost)
