var code_send = false;
var last_transport = '';
var auth_div;
var user_hash='changeit';
var getUserResponse;
var socket;
var lt = document.getElementsByName("lt")[0].value;

var font_awesome ={
    transport:{
        sms:"&#xf10b;",
        push:"&#xf09e;",
        mail:"&#xf0e0;"
    }
};

var state = 0;
$(document).keypress(function(event){
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        switch(state){
            case 0:$('#buttonMethods').click();break;
            case 1:$('#ownCodeInput').click();break;
            case 2:$('#submit').click();break;
            default: console.log('You pressed a "enter" key but nothing happen');break;
        }

    }
});

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
            if(method == 'push')send_code_push(transport);
            else send_code_classic(transport, method);
        } else errors_message(strings.error.login_needed);
    } else {
        errors_message(strings.error.transport_wait + ' ' + last_transport);
    }
};

function send_code_classic(transport, method){
    request({ method: 'POST', url: url_esup_otp + '/users/'+ document.getElementById('usernameLabel').innerHTML +'/methods/'+ method +'/transports/'+ transport +'/'+ user_hash }, function(response) {
        if (response.code == "Ok") {
            success_message(strings.success.transport + transport);
            hide_methods();
        } else {
            errors_message(strings.error.message + response.message);
        }
        code_send = false;
    });
}

function send_code_push(transport) {
    request({ method: 'POST', url: url_esup_otp + '/users/'+ document.getElementById('usernameLabel').innerHTML +'/methods/push/transports/'+ transport +'/'+lt+'/'+ user_hash }, function(response) {
        if (response.code == "Ok") {
            success_message(strings.success.transport + transport);
            hide_methods();
            socket = io.connect(url_esup_otp, {reconnect: true, path: "/sockets", query: 'uid='+document.getElementById('usernameLabel').innerHTML+'&hash='+user_hash+'&app=cas'});
            initialize_socket();
        } else {
            errors_message(strings.error.message + response.message);
        }
        code_send = false;
    });
}

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
    request({ method: 'GET', url: url_esup_otp + '/users/' + document.getElementById('username').value + '/' + user_hash }, function(response) {
        if (response.code == "Ok") {
            $('#own-code').show();
            $('#instructions_username').hide();
            $('#instructions_transport').show();
            state =1;
            getUserResponse = response;
            methods_labels();
            transports_labels();
        } else {
            errors_message(strings.error.message + response.message);
        }
    });
};

function transports_labels(){
    if (!getUserResponse.user.transports.sms) {
        $('.sms').remove();
    } else {
        $('.label-sms').val(strings.label.sms + getUserResponse.user.transports.sms+' '+'\uf10b');
    }
    if (!getUserResponse.user.transports.mail) {
        $('.mail').remove();
    } else {
        $('.label-mail').val(strings.label.mail + getUserResponse.user.transports.mail+' '+'\uf0e0');
    }
    if (!getUserResponse.user.transports.push) {
        $('.push').remove();
    } else {
        $('.label-push').val(strings.label.push + getUserResponse.user.transports.push+' '+'\uf10b');
    }
    $('#list-methods').show();
    var username = document.getElementById('username').value;
    $('#username').hide();
    document.getElementById("buttonMethods").style.display="none";
    $('#usernameLabel').empty();
    $('#usernameLabel').html(username);
    //document.getElementById("resetUsername").style.display="inline-block";
    reset_message();
}

function methods_labels() {
    var methods_exist = false;
    var transports_exist = false;
    for (method in getUserResponse.user.methods) {
        if (getUserResponse.user.methods[method].active && getUserResponse.user.transports!={}) {
            methods_exist = true;
            for(transport in getUserResponse.user.methods[method].transports){
                if(transport==0 && getUserResponse.user.transports[getUserResponse.user.methods[method].transports[transport]])$('#list-methods').append("<h3 style='margin-top:15px;'>" + strings.method[method] + "</h3>");
                if(getUserResponse.user.transports[getUserResponse.user.methods[method].transports[transport]]){
                    transports_exist = true;
                    $('#list-methods').append("<div class='method-row "+getUserResponse.user.methods[method].transports[transport]+"'><input class='button transport label"+getUserResponse.user.methods[method].transports[transport]+"' type='button' value='"+getUserResponse.user.transports[getUserResponse.user.methods[method].transports[transport]]+" "+font_awesome.transport[getUserResponse.user.methods[method].transports[transport]]+"' onclick='send_code(\""+getUserResponse.user.methods[method].transports[transport]+"\", \"" + method + "\");'></div>");
                }
            }
        }
    }
    $('#list-methods').show();
    if (!methods_exist || !transports_exist) {

        show_auth_form();
        $('#lost-code').hide();
    }
}


function init() {
    auth_div = $('#auth');
    $('#auth').remove();
    $('#auth-option').hide();
    $('#list-methods').hide();
    $('#resetUsername').hide();
    get_user_auth();
};

function success_message(message) {
    $('#msg2').attr('class', 'alert alert-success');
    $('#msg2').html(message);
    $('#msg2').show();
    $("#msg2").fadeTo(3500, 500).slideUp(300, function(){
        $("#msg2").hide();
    });
    $('.close').hide()
}

function errors_message(message) {
    $('#msg2').attr('class', 'alert alert-danger');
    $('#msg2').html(message);
    $("#msg2").fadeTo(3500, 500).slideUp(300, function(){
        $("#msg2").hide();
    });
    $('.close').hide()
}

function reset_message() {
    $('#msg2').html('');
    $('#msg2').hide();
}

function reset_username() {
    $('#lost-code').hide();
    $('#list-methods').hide();
    $('#list-methods').empty();
    $('#auth').hide();
    $('#auth-option').hide();
    $('#msg2').hide();
    $('#submit').attr('type', '');
    document.getElementById("resetUsername").style.display="none";
    $('#usernameLabel').html('');
    $('#username').show();
    document.getElementById("buttonMethods").style.display="inline-block";
}

function hide_methods() {
    show_auth_form();
    $('#list-methods').hide();
    $('#own-code').hide();
}

function show_methods() {
    state =1;
    $('#list-methods').show();
    $('#auth-option').hide();
    $('#auth').hide();
    $('#lost-code').hide();
    $('#own-code').show();
    $('#instructions_transport').show();
    $('#instructions_code').hide();
}

function show_auth_form(){
    state =2;
    show_auth_option();
    $('#auth').show();
    if(getUserResponse.user.methods.waitingFor && !getUserResponse.user.methods.codeRequired)$('#submit').hide();
    if(getUserResponse.user.methods.codeRequired)$('#password').show();
    else {
        $('#password').hide();
        if(!getUserResponse.user.methods.waitingFor && !getUserResponse.user.methods.codeRequired)$('#password').val("bypass");
    }
    $('#lost-code').show();
    $('#instructions_transport').hide();
    $('#instructions_code').show();
}

function show_auth_option(){
    state =2;
    $('#own-code').hide();
    $('#auth-option').show();
    auth_div.insertAfter('#list-methods');
    $('#auth').hide();
    $('#submit').attr('type', 'submit');
}

function check_auth(){
    request({method:'GET', url : url_esup_otp + '/users/'+ document.getElementById('usernameLabel').innerHTML +'/methods/push/'+lt+'/'+user_hash}, function(response){
        if (response.code == "Ok") {
            $('#password').val(response.otp);
            $('#submit').click();
        }
    })
}

function initialize_socket() {
    socket.on('connect', function () {
    });

    socket.on('userAuth', function () {
        check_auth();
    });
}