var code_send = false;
var last_transport = '';
var auth_div;
var methods_requested = false;

function send_code(transport) {
    if (!code_send) {
        if (document.getElementById('usernameLabel').innerHTML != '') {
            code_send = true;
            last_transport = transport;
            var req = new XMLHttpRequest();
            req.open('GET', 'https://tequila:3443/send_code/google_authenticator/' + transport + '/' + document.getElementById('usernameLabel').innerHTML, true);
            req.onerror = function(e) {
                errors_message("Erreur :" + e.target.status);
                code_send = false;
            };
            req.onreadystatechange = function(aEvt) {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        var responseObject = JSON.parse(req.responseText);
                        if (responseObject.code == "Ok") {
                            success_message('Envoi du code via ' + transport);
                            hide_methods();
                        } else {
                            errors_message('Erreur ' + responseObject.message);
                        }
                        code_send = false;
                    }
                }
            };
            req.send(null);
        } else errors_message('Veuillez entrer votre login');
    } else {
        errors_message("Vous devez attendre l'envoi du code via " + last_transport);
    }
};

function get_available_methods() {
    if(!methods_requested){
        var req = new XMLHttpRequest();
        req.open('GET', 'https://tequila:3443/get_available_methods', true);
        req.onerror = function(e) {
            console.log(e);
        };
        req.onreadystatechange = function(aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var responseObject = JSON.parse(req.responseText);
                    if (responseObject.code == "Ok") {
                        $('#list-methods').prepend("<p class='button success' onclick='hide_methods();'>"+"J'ai déjà un code <i class='fa fa-key'></i>"+"</p>");
                        for (method in responseObject.methods) {
                            $('#list-methods').append("<div id='" + responseObject.methods[method] + "'></div>");
                            $('#' + responseObject.methods[method]).append("<h3>" + responseObject.methods[method] + "</h3>");
                            $('#' + responseObject.methods[method]).append("<div class='method-row sms'><p class='label label-sms'></p><p class='button transport' onclick='send_code(\"sms\");'>Sms <i class='fa fa-mobile'></i></p></div>");
                            $('#' + responseObject.methods[method]).append("<div class='method-row mail'><p class='label label-mail'></p><p class='button transport' onclick='send_code(\"mail\");'>Mail <i class='fa fa-envelope'></i></p></div>");
                            methods_requested = true;
                        }
                    } else {
                        errors_message('Erreur ' + responseObject.message);
                    }
                }
            }
        };
        req.send(null);
    }else{
        $('#list-methods').show();
    }
};

function get_available_transports() {
    $('#auth-option').hide();
    if (document.getElementById('username').value != '') {
        var req = new XMLHttpRequest();
        req.open('GET', 'https://tequila:3443/get_available_transports/' + document.getElementById('username').value, true);
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
                        }else{
                            $('.label-sms').html("Sms to "+responseObject.transports_list.sms);
                        }
                        if (!responseObject.transports_list.mail) {
                            $('.mail').remove();
                        }else{
                            $('.label-mail').html("Mail to "+responseObject.transports_list.mail);
                        }
                        $('#list-methods').show();
                        var username = document.getElementById('username').value;
                        $('#username').hide();
                        $('#buttonMethods').hide();
                        $('#usernameLabel').html(username);
                        $('#resetUsername').show();
                        reset_message();
                    }else{
                        errors_message('Error :'+responseObject.message);
                    }
                }
            }
        };
        req.send(null);
    } else errors_message("Veuillez entrer votre login");
};



function init() {
    $('#login').prepend('<div id="msg2" class="errors"></div>');
    $('#msg2').hide();
    $('#resetUsername').hide();
    auth_div = $('#auth');
    $('#auth-option').hide();
    $('#auth').remove();
    $('#list-methods').hide();
    get_available_methods();
};

function success_message(message){
    $('#msg2').attr('class', 'success');
    $('#msg2').attr('style', 'background-color: rgb(221, 255, 170); color: #33691E;');
    $('#msg2').html(message);
    $('#msg2').show();
}

function errors_message(message){
    $('#msg2').attr('class', 'errors');
    $('#msg2').attr('style', 'background-color: rgb(255, 238, 221); color: #DD2C00;');
    $('#msg2').html(message);
    $('#msg2').show();
}

function reset_message(){
    $('#msg2').html('');
    $('#msg2').hide();
}

function reset_username(){
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


// <div style="background-color: rgb(221, 255, 170);" id="msg" class="success">
//     <h2>Logout successful</h2>
//     <p>You have successfully logged out of the Central Authentication Service.</p>
//     <p>For security reasons, exit your web browser.</p>
//   </div>

// <div id="list-transports">
//   <div class="list-transports">
//   <h3>Veuillez choisir le moyen par lequel vous souhaitez recevoir votre code temporaire</h3>
//   Votre login <input type="text" id="loginUser" name="LastName">
//   <p id="buttonMail" onclick="send_code('mail')" class="button">Mail</p> <p id="buttonSms" onclick="send_code('sms')" class="button" >Sms</p> <p id="buttonApp" onclick="send_code('app')" class="button">Application smartphone</p>
// </div>
// </div>

//    <p onclick="$('#list-transports').show();" class="button">Renvoyer code</p>
