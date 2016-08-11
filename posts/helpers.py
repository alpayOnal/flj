from django.http import HttpResponse
from rest_framework import exceptions
from rest_framework.compat import set_rollback
from rest_framework.renderers import JSONRenderer
from rest_framework.response import Response
from rest_framework.views import exception_handler


class JSONResponse(HttpResponse):
    def __init__(self, data, **kwargs):
        super(JSONResponse, self).__init__()
        content = JSONRenderer().render(data)
        kwargs["content_type"] = "application/json"
        super(JSONResponse, self).__init__(content, **kwargs)


def my_exception_handler(exc, context):
    if (
                isinstance(exc, exceptions.APIException) and
                isinstance(exc.detail, (list, dict))
    ):
        detail = " - ".join(
            ["{}: {}".format(k, v[0]) for k, v in exc.detail.items()])
        response = {"error": detail}
        set_rollback()
        return Response(response, status=exc.status_code)
    else:
        exception_handler(exc, context)