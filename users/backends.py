from django.contrib.auth import get_user_model


class TokenAuth(object):
    """
    Token based django authenticator
    """

    def authenticate(self, username=None, credential=None, **kwargs):
        UserModel = get_user_model()
        try:
            user = UserModel._default_manager.get_by_natural_key(username)
            if (
                        user.userprofile.check_credential(credential) and
                        self.user_can_authenticate(user)
            ):
                return user
        except UserModel.DoesNotExist:
            return

    def user_can_authenticate(self, user):
        """
        Reject users with is_active=False. Custom user models that don't have
        that attribute are allowed.
        """
        is_active = getattr(user, 'is_active', None)
        return is_active or is_active is None

    def get_user(self, user_id):
        UserModel = get_user_model()
        try:
            user = UserModel._default_manager.get(pk=user_id)
        except UserModel.DoesNotExist:
            return None
        return user if self.user_can_authenticate(user) else None


class TokenAuthREST(TokenAuth):
    """
    Authenticator bakcend for django rest framework
    """

    def authenticate(self, request):
        """

        :param request: django rest request
        :return: default user model instance
        """
        UserModel = get_user_model()
        try:
            username = request.META['HTTP_X_USERNAME']
            credential = request.META['HTTP_X_CREDENTIAL']
            user = UserModel._default_manager.get_by_natural_key(username)
            if (
                        user.userprofile.check_credential(credential) and
                        self.user_can_authenticate(user)
            ):
                return user, None
        except (KeyError, UserModel.DoesNotExist):
            return
