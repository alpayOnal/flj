import random

import requests
from django.contrib.auth import authenticate, login
from django.contrib.auth.models import User
from django.http import HttpResponse
from django.http import JsonResponse

from django.shortcuts import redirect
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from rest_framework.response import Response

from posts.models import UserProfile
from posts.serializers import UserSerializer


@csrf_exempt
def verifyGoogleSignin(http_request):
    """
    verify google signin token and if verified, returns user entity

    if token is verified via google service, current user associated with
    email address is returned. if use does not exists, a new one created and
    returned.

    :param http_request:
    :return:
    """
    token = http_request.POST["token"]
    url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token={}".format(
        token)

    data = requests.get(url).json()
    email = data["email"]
    given_name = data["given_name"]
    family_name = data["family_name"]
    picture = data["picture"]

    try:
        user = User.objects.get(email=email)
        userprofile = user.userprofile
        credential = userprofile.credential
    except ObjectDoesNotExist:
        user = User()
        user.email = email
        user.username = email
        userprofile = UserProfile()
        credential = userprofile.generate_credential()

    user.first_name = given_name
    user.last_name = family_name
    user.save()

    userprofile.credential = credential
    userprofile.picture = picture
    userprofile.user = user
    userprofile.save()

    # because getUser needs authentication, login process is here.
    user = authenticate(username=email, credential=credential)
    login(http_request, user)
    # return redirect("getUser", 1)
    return JsonResponse(UserSerializer(user).data)


@csrf_exempt
def verifyFacebookSignin(http_request):
    token = http_request.POST["token"]
    fbProfileId = http_request.POST["profileId"]
    url = "https://graph.facebook.com/{}?fields=id," \
          "name,birthday,email,gender,hometown,location," \
          "picture&access_token={}".format(fbProfileId, token)
    print(url)
    data = requests.get(url).json()
    nameParts = data["name"].split(" ")
    email = data["email"]
    given_name = nameParts[0]
    family_name = nameParts[1] if len(nameParts) > 1 else ""
    picture = data["picture"]["data"]["url"]

    try:
        user = User.objects.get(email=email)
        userprofile = user.userprofile
        credential = userprofile.credential
    except ObjectDoesNotExist:
        user = User()
        user.email = email
        user.username = email
        userprofile = UserProfile()
        credential = userprofile.generate_credential()

    user.first_name = given_name
    user.last_name = family_name
    user.save()

    userprofile.credential = credential
    userprofile.picture = picture
    userprofile.user = user
    userprofile.save()

    # because getUser needs authentication, login process is here.
    user = authenticate(username=email, credential=credential)
    login(http_request, user)
    return JsonResponse(UserSerializer(user).data)
