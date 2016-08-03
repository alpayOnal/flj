import requests
from django.contrib.auth import authenticate, login
from django.http import JsonResponse
from django.shortcuts import redirect
from django.views.decorators.csrf import csrf_exempt
from posts.models import UserProfile
from posts.serializers import UserSerializer


def redirectToUser(http_request, user):
    # because getUser needs authentication, login process is here.
    user = authenticate(
        username=user.username,
        credential=user.userprofile.credential)
    login(http_request, user)
    return redirect("getUser", user.id)


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

    user = UserProfile.getOrCreateUser(
        username=email,
        email=email,
        first_name=given_name,
        last_name=family_name,
        picture=picture
    )
    return JsonResponse(UserSerializer(user).data)


@csrf_exempt
def verifyFacebookSignin(http_request):

    token = http_request.POST["token"]
    fbProfileId = http_request.POST["profileId"]
    url = "https://graph.facebook.com/{}?fields=id," \
          "name,birthday,email,gender,hometown,location," \
          "picture&access_token={}".format(fbProfileId, token)

    data = requests.get(url).json()
    nameParts = data["name"].split(" ")
    email = data["email"]
    first_name = nameParts[0]
    last_name = nameParts[1] if len(nameParts) > 1 else ""
    picture = data["picture"]["data"]["url"]

    user = UserProfile.getOrCreateUser(
        username=email,
        email=email,
        first_name=first_name,
        last_name=last_name,
        picture=picture
        )

    return JsonResponse(UserSerializer(user).data)
