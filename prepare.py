import django
django.setup()
from django.contrib.auth.models import User
from posts.models import UserProfile, JobPost
from django.contrib.auth import authenticate
from posts.serializers import JobPostSerializer, UserSerializer


u = User(username="a", email="a@a.com")
u.set_password("123456")
up = UserProfile(user=u)
jp = JobPost(title="python dev", description="python dev araniyor")
