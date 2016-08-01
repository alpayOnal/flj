import random

import requests
from django.contrib.auth import login, authenticate
from django.contrib.auth.models import User
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from posts.models import UserProfile


@csrf_exempt
def verify(http_request):
    token = http_request.POST["token"]
    url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token={}".format(
        token)

    data = requests.get(url).json()
    email = data["email"]
    given_name = data["given_name"]
    family_name = data["family_name"]
    picture = data["picture"]


    from django.core.exceptions import ObjectDoesNotExist

    try:
        user = User.objects.get(email=email)
        userprofile = user.userprofile
        credential = userprofile.credential
    except ObjectDoesNotExist:
        user = User()
        user.email = email
        user.username = email
        userprofile = UserProfile(user=user)
        credential = random.randint(100, 1000)

    user.first_name = given_name
    user.last_name = family_name
    user.userprofile.picture = picture
    user.save()
    userprofile.user = user
    userprofile.credential = credential
    userprofile.save()
    # authenticate(email=email, token=user.userprofile.token)
    # login(http_request, user)
    return HttpResponse(credential)
