var Subscription = {
    selector: ".subscription-card"
};

Subscription.show = function(data) {
    var data = $.extend({
        title: "Be the first to know",
        description: "Save this search and receive jobs like these every day.",
        email: "",
        className: "subscription-search"
    }, data);
    var template = $('#subscription').html();
    Mustache.parse(template);   // optional, speeds up future uses
    var rendered = Mustache.render(template, data);
    $(Subscription.selector).html(rendered);
    Subscription.bind()
}


Subscription.bind = function($form) {
    $(Subscription.selector).submit(Subscription.onSubmit)
}

Subscription.onSubmit = function(e) {
    var email = $(Subscription.selector).find('input[name="email"]').val();
    console.log(email)
    e.preventDefault();
    return false;
}