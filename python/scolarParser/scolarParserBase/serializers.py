from rest_framework import serializers
from .models import ParsedDocument

class ParsedDocumentSerializer(serializers.ModelSerializer):
    class Meta:
        model = ParsedDocument
        fields = ('title', 'pages', 'pub_year', 'author')