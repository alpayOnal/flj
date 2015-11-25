$(document).ready(function(){
   if ($(".no-jobs-found").length ) {
    Subscription.show({
        description: "Save this search and receive jobs results when it gets available"
    })
   } else {
    Subscription.show()
   }

});

