var code_send = false;
var last_transport = '';
var auth_div;
var user_hash='changeit';

function request(opts, callback, next) {
    var req = new XMLHttpRequest();
    req.open(opts.method, opts.url, true);
    req.onerror = function(e) { 
        console.log(e);
        code_send = false;
    };
    req.onreadystatechange = function(aEvt) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseObject = JSON.parse(req.responseText);
                if (typeof(callback) === "function") callback(responseObject);
            }
            if (typeof(next) === "function") next();
        }
    };
    req.send(null);
}


function send_code(transport, method) {
    if (!code_send) {
        if (document.getElementById('usernameLabel').innerHTML != '') {
            code_send = true;
            last_transport = transport;
            request({ method: 'GET', url: url_esup_otp + '/user/'+ document.getElementById('usernameLabel').innerHTML +'/method/'+ method +'/transport/'+ transport +'/code/send/'+ user_hash }, function(response) {
                if (response.code == "Ok") {
                    success_message(strings.success.transport + transport);
                    hide_methods();
                } else {
                    errors_message(strings.error.message + response.message);
                }
                code_send = false;
            });
        } else errors_message(strings.error.login_needed);
    } else {
        errors_message(strings.error.transport_wait + ' ' + last_transport);
    }
};



function get_user_auth() {
	if(document.getElementById('username')){
    if (document.getElementById('username').value != '') {
    		user_hash = getUserHash();
            get_user_infos();
    } else errors_message(strings.error.login_needed);
    }
}

function get_user_infos() {
    $('#auth-option').hide();
    request({ method: 'GET', url: url_esup_otp + '/user/' + document.getElementById('username').value + '/' + user_hash }, function(response) {
        if (response.code == "Ok") {
        	methods_labels(response);
        	transports_labels(response);
        } else {
            errors_message(strings.error.message + response.message);
        }
    });
};

function transports_labels(data){
	if (!data.user.transports.sms) {
        $('.sms').remove();
    } else {
        $('.label-sms').html(strings.label.sms + data.user.transports.sms);
    }
    if (!data.user.transports.mail) {
        $('.mail').remove();
    } else {
        $('.label-mail').html(strings.label.mail + data.user.transports.mail);
    }
    $('#list-methods').show();
    var username = document.getElementById('username').value;
    $('#username').hide();
    $('#buttonMethods').hide();
    $('#usernameLabel').empty();
    $('#usernameLabel').html(username);
    $('#resetUsername').show();
    reset_message();
}

function methods_labels(data) {
    var methods_exist = false;
    for (method in data.user.methods) {
        if (data.user.methods[method].active) {
            if (!methods_exist) $('#list-methods').prepend("<p class='button success' onclick='hide_methods();'>" + strings.button.code.owned + "<i class='fa fa-key'></i>" + "</p>");
            methods_exist = true;
            if (data.user.methods[method].transports.indexOf('sms') >= 0 || data.user.methods[method].transports.indexOf('mail') >= 0) {
                $('#list-methods').append("<h3>" + strings.method[method] + "</h3>");
                if (data.user.methods[method].transports.indexOf('sms') >= 0) $('#list-methods').append("<div class='method-row sms'><p class='label label-sms'></p><p class='button transport' onclick='send_code(\"sms\", \"" + method + "\");'>" + strings.button.send.sms + "<i class='fa fa-mobile'></i></p></div>");
                if (data.user.methods[method].transports.indexOf('mail') >= 0) $('#list-methods').append("<div class='method-row mail'><p class='label label-mail'></p><p class='button transport' onclick='send_code(\"mail\", \"" + method + "\");'>" + strings.button.send.mail + " <i class='fa fa-envelope'></i></p></div>");
            }
            $('#list-methods').show();
        }
        if (!methods_exist) show_auth_option();
    }
}


function init() {
    auth_div = $('#auth');
    $('#auth').remove();
    $('#auth-option').hide();
    $('#list-methods').hide();
    $('#resetUsername').hide();
    $('#login').prepend('<div id="msg2" class="errors"></div>');
    $('#msg2').hide();
    get_user_auth();
};

function success_message(message) {
    $('#msg2').attr('class', 'success');
    $('#msg2').attr('style', 'background-color: rgb(221, 255, 170); color: #33691E;');
    $('#msg2').html(message);
    $('#msg2').show();
}

function errors_message(message) {
    $('#msg2').attr('class', 'errors');
    $('#msg2').attr('style', 'background-color: rgb(255, 238, 221); color: #DD2C00;');
    $('#msg2').html(message);
    $('#msg2').show();
}

function reset_message() {
    $('#msg2').html('');
    $('#msg2').hide();
}

function reset_username() {
    $('#list-methods').hide();
    $('#list-methods').empty();
    $('#auth').hide();
    $('#auth-option').hide();
    $('#msg2').hide();
    $('#submit').attr('type', '');
    $('#resetUsername').hide();
    $('#usernameLabel').html('');
    $('#username').show();
    $('#buttonMethods').show();

}

function hide_methods() {
	show_auth_form();
    $('#list-methods').hide();
}

function show_auth_form(){
	show_auth_option();
    $('#auth').show();
}

function show_auth_option(){
    $('#auth-option').show();
    auth_div.insertBefore('#auth-option');
    $('#auth').hide();
    $('#submit').attr('type', 'submit');
}