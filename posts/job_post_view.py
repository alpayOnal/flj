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
        keyword = self.request.query_params.get("keyword")
        country = self.request.query_params.get("country")
        city = self.request.query_params.get("city")
        sinceId = self.request.query_params.get("sinceId")
        maxId = self.request.query_params.get("maxId")
        # lat = self.request.query_params.get("latitude")
        # long = self.request.query_params.get("longitude")

        criteria = []
        if keyword:
            criteria.append(
                Q(title__icontains=keyword) | Q(description__icontains=keyword))
        if country and city:
            criteria.append(Q(country=country) & Q(city=city))
        if sinceId:
            criteria.append(Q(job_id__gt=sinceId))
        if maxId:
            criteria.append(Q(job_id__lt=maxId))
        # elif lat and long:
        #     point = GEOSGeometry('POINT(%s %s)' % (lat, long), srid=4326)
        #     criteria.append(Q(point__distance_lte=(point, D(m=5))))
        queryset = JobPost.objects\
            .filter(*criteria)\
            .order_by("-created_at")[:9]
        return queryset


class JobPostDetail(generics.RetrieveUpdateDestroyAPIView):
    queryset = JobPost.objects.all()
    serializer_class = JobPostSerializer

