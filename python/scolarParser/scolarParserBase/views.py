from django.forms import model_to_dict
from rest_framework.response import Response
from rest_framework.views import APIView
from scholarly import scholarly, ProxyGenerator

from .models import ParsedDocument


class ParsedDocumentApiView(APIView):
    def post(self, request):
        title = request.query_params['title']
        try:
            document_from_db = ParsedDocument.objects.get(title=title)
            return Response(model_to_dict(document_from_db))
        except:
            if request.query_params['use_proxy']:
                pg = ProxyGenerator()
                pg.ScraperAPI('b8e66b3819acb1957aab0e6d5e3ac400')
                scholarly.use_proxy(pg)
            search_query = scholarly.search_pubs(title)
            publication = scholarly.fill(next(search_query))

            document = ParsedDocument.objects.create(
                title=publication['bib']['title'],
                author=publication['bib']['author'],
                pub_year=publication['bib']['pub_year'],
                pages=publication.get('bib').get('pages'),
            )
            return Response(model_to_dict(document))
