from django.http import HttpResponse

# Make sure bv is in your $PYTHONPATH
# In this case, I've symlinked bv.py from ../../../bvseosdk/bv.py
import bv

def home(request):

    bvSeo = bv.BV(
        deployment_zone_id='12325',
        product_id='product1',
        page_url=request.build_absolute_uri(),
        cloud_key='agileville-78B2EF7DE83644CAB5F8C72F2D8C8491',
        bv_product='reviews',
        user_agent='msnbot',
        current_request_query_string=request.GET.copy().dict()
    )

    return HttpResponse(bvSeo.getSeoWithDisplay())