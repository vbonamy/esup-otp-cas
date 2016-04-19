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
            request({ method: 'GET', url: url_esup_otp + '/send_code/' + method + '/' + transport + '/' + document.getElementById('usernameLabel').innerHTML + '/' + user_hash }, function(response) {
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
    if (document.getElementById('username').value != '') {
            if(!mfa)user_hash = generate_hash(document.getElementById('username').value);
            else user_hash = document.getElementById('user_hash').value;
            get_available_methods();
            get_available_transports();
    } else errors_message(strings.error.login_needed);
}

function get_available_methods() {
    request({ method: 'GET', url: url_esup_otp + '/activate_methods/' + document.getElementById('username').value + '/' + user_hash }, function(response) {
        if (response.code == "Ok") {
            var methods_exist = false;
            $('#list-methods').prepend("<p class='button success' onclick='hide_methods();'>" + strings.button.code.owned + "<i class='fa fa-key'></i>" + "</p>");
            for (method in response.methods) {
                if (response.methods[method]) {
                    methods_exist = true;
                    $('#list-methods').append("<h3>" + strings.method[method] + "</h3>");
                    if (response.methods[method].sms) $('#list-methods').append("<div class='method-row sms'><p class='label label-sms'></p><p class='button transport' onclick='send_code(\"sms\", \"" + method + "\");'>" + strings.button.send.sms + "<i class='fa fa-mobile'></i></p></div>");
                    if (response.methods[method].mail) $('#list-methods').append("<div class='method-row mail'><p class='label label-mail'></p><p class='button transport' onclick='send_code(\"mail\", \"" + method + "\");'>" + strings.button.send.mail + " <i class='fa fa-envelope'></i></p></div>");
                    $('#list-methods').show();
                }
                if(!methods_exist)document.getElementById("fm1").submit();
            }
        } else {
            errors_message(strings.error.message + ' ' + response.message);
        }
    });
};


function get_available_transports() {
    $('#auth-option').hide();
    request({ method: 'GET', url: url_esup_otp + '/available_transports/' + document.getElementById('username').value + '/' + user_hash }, function(response) {
        if (response.code == "Ok") {
            if (!response.transports_list.sms) {
                $('.sms').remove();
            } else {
                $('.label-sms').html(strings.label.sms + response.transports_list.sms);
            }
            if (!response.transports_list.mail) {
                $('.mail').remove();
            } else {
                $('.label-mail').html(strings.label.mail + response.transports_list.mail);
            }
            $('#list-methods').show();
            var username = document.getElementById('username').value;
            $('#username').hide();
            $('#buttonMethods').hide();
            $('#usernameLabel').empty();
            $('#usernameLabel').html(username);
            $('#resetUsername').show();
            reset_message();
        } else {
            errors_message(strings.error.message + response.message);
        }
    });
};




function init() {
    auth_div = $('#auth');
    $('#auth').remove();
    $('#auth-option').hide();
    $('#list-methods').hide();
    $('#resetUsername').hide();
    $('#login').prepend('<div id="msg2" class="errors"></div>');
    $('#msg2').hide();
    if(mfa)get_user_auth();
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
    $('#auth-option').show();
    auth_div.insertBefore('#auth-option');
    $('#auth').show();
    $('#submit').attr('type', 'submit');
    $('#list-methods').hide();
}
