from django.contrib.gis.measure import D
from django.db.models import Q
from posts.helpers import JSONResponse
from posts.models import JobPost
from posts.permissions import IsOwnerOrReadOnly
from posts.serializers import JobPostSerializer
from rest_framework import generics
from rest_framework import permissions
from django.contrib.gis.geos import GEOSGeometry

class JobPosts(generics.ListCreateAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer
    # permission_classes = (IsOwnerOrReadOnly,)
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def get_queryset(self):
        # location, keyword
        # if self.request.get("")
        keyword = self.request.query_params.get("keyword")
        country = self.request.query_params.get("country")
        city = self.request.query_params.get("city")
        lat = self.request.query_params.get("latitude")
        long = self.request.query_params.get("longitude")

        criteria = []
        if keyword:
            criteria.append(
                Q(title__icontains=keyword) | Q(description__icontains=keyword))
        if country and city:
            criteria.append(Q(country=country) & Q(city=city))
        elif lat and long:
            point = GEOSGeometry('POINT(%s %s)' % (lat, long), srid=4326)
            criteria.append(Q(point__within=point))
        queryset = JobPost.objects.filter(*criteria).order_by("-created_at")
        # return JobPost.objects.all()
        return queryset


class JobPostDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer

