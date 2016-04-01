var code_send = false;
var last_transport = '';
var auth_div;
var methods_requested = false;
var user_hash;

function send_code(transport, method) {
    if (!code_send) {
        if (document.getElementById('usernameLabel').innerHTML != '') {
            code_send = true;
            last_transport = transport;
            var req = new XMLHttpRequest();
            req.open('GET', url_esup_otp + '/send_code/' + method + '/' + transport + '/' + document.getElementById('usernameLabel').innerHTML + '/' + user_hash, true);
            req.onerror = function(e) {
                errors_message(strings.error.message + e.target.status);
                code_send = false;
            };
            req.onreadystatechange = function(aEvt) {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        var responseObject = JSON.parse(req.responseText);
                        if (responseObject.code == "Ok") {
                            success_message(strings.success.transport + transport);
                            hide_methods();
                        } else {
                            errors_message(strings.error.message + responseObject.message);
                        }
                        code_send = false;
                    }
                }
            };
            req.send(null);
        } else errors_message(strings.error.login_needed);
    } else {
        errors_message(strings.error.transport_wait + ' ' + last_transport);
    }
};

function get_user_auth() {
    if (document.getElementById('username').value != '') {
        TwinBcrypt.hash(document.getElementById('username').value + salt_esup_otp, TwinBcrypt.genSalt(4), function(hash) {
            hash = hash.replace(/\//g, "%2F");
            user_hash = hash;
            get_available_methods();
            get_available_transports();

        })
    } else errors_message(strings.error.login_needed);
}

function get_available_methods() {
    if (!methods_requested) {
        var req = new XMLHttpRequest();
        req.open('GET', url_esup_otp + '/activate_methods/' + document.getElementById('username').value + '/' + user_hash, true);
        req.onerror = function(e) {
            console.log(e);
        };
        req.onreadystatechange = function(aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var responseObject = JSON.parse(req.responseText);
                    if (responseObject.code == "Ok") {
                        $('#list-methods').prepend("<p class='button success' onclick='hide_methods();'>" + strings.button.code.owned + "<i class='fa fa-key'></i>" + "</p>");
                        for (method in responseObject.methods) {
                            if (responseObject.methods[method]) {
                                $('#list-methods').append("<h3>" + strings.method[method] + "</h3>");
                                if (responseObject.methods[method].sms) $('#list-methods').append("<div class='method-row sms'><p class='label label-sms'></p><p class='button transport' onclick='send_code(\"sms\", \"" + method + "\");'>" + strings.button.send.sms + "<i class='fa fa-mobile'></i></p></div>");
                                if (responseObject.methods[method].mail) $('#list-methods').append("<div class='method-row mail'><p class='label label-mail'></p><p class='button transport' onclick='send_code(\"mail\", \"" + method + "\");'>" + strings.button.send.mail + " <i class='fa fa-envelope'></i></p></div>");
                                methods_requested = true;
                            }
                        }
                    } else {
                        errors_message(strings.error.message + ' ' + responseObject.message);
                    }
                }
            }
        };
        req.send(null);
    } else {
        $('#list-methods').show();
    }
};

function get_available_transports() {
    $('#auth-option').hide();
    var req = new XMLHttpRequest();
    req.open('GET', url_esup_otp + '/available_transports/' + document.getElementById('username').value + '/' + user_hash, true);
    req.onerror = function(e) {
        console.log(e);
    };
    req.onreadystatechange = function(aEvt) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseObject = JSON.parse(req.responseText);
                if (responseObject.code == "Ok") {
                    if (!responseObject.transports_list.sms) {
                        $('.sms').remove();
                    } else {
                        $('.label-sms').html(strings.label.sms + responseObject.transports_list.sms);
                    }
                    if (!responseObject.transports_list.mail) {
                        $('.mail').remove();
                    } else {
                        $('.label-mail').html(strings.label.mail + responseObject.transports_list.mail);
                    }
                    $('#list-methods').show();
                    var username = document.getElementById('username').value;
                    $('#username').hide();
                    $('#buttonMethods').hide();
                    $('#usernameLabel').html(username);
                    $('#resetUsername').show();
                    reset_message();
                } else {
                    errors_message(strings.error.message + responseObject.message);
                }
            }
        }
    };
    req.send(null);

};



function init() {
    auth_div = $('#auth');
    $('#auth').remove();
    $('#auth-option').hide();
    $('#list-methods').hide();
    $('#resetUsername').hide();
    $('#login').prepend('<div id="msg2" class="errors"></div>');
    $('#msg2').hide();
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
