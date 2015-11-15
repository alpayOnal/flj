from functools import wraps
from flask import request, Response
from app.libraries.loggerFactory import getLogger

logger = getLogger()


def isAdmin(username, password):
        """This function is called to check if a username /
        password combination is valid.
        """
        authenticated = username == 'admin' and password == 'admin'
        if not authenticated:
            logger.debug("admin authentication failed. {} {}".format(
                username, password))
        return authenticated


def authenticateAdmin():
    """Sends a 401 response that enables basic auth"""
    return Response(
        'Could not verify your access level for that URL.\n'
        'You have to login with proper credentials', 401,
        {'WWW-Authenticate': 'Basic realm="Login Required"'}
    )


def requires_admin(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or not isAdmin(auth.username, auth.password):
            return authenticateAdmin()
        return f(*args, **kwargs)
    return decorated
