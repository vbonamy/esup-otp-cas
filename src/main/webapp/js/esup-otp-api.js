var code_send = false;
var last_transport = '';

function send_code(transport) {
    if (!code_send) {
        if (document.getElementById('username').value != '') {
            code_send = true;
            last_transport = transport;
            var req = new XMLHttpRequest();
            req.open('GET', 'https://tequila:3443/send_code/google_authenticator/' + transport + '/' + document.getElementById('username').innerHTML, true);
            req.onerror = function(e) {
                alert("Erreur :" + e.target.status);
                code_send = false;
            };
            req.onreadystatechange = function(aEvt) {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        var responseObject = JSON.parse(req.responseText);
                        if (responseObject.code == "Ok") {
                            alert('Envoi du code via ' + transport);
                        } else {
                            alert('Erreur ' + responseObject.message);
                        }

                        code_send = false;
                    }
                }
            };
            req.send(null);
        } else alert("Veuillez entrer votre login");
    } else {
        alert("Vous devez attendre l'envoi du code via " + last_transport);
    }
};

function get_available_methods() {
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
                    for(method in responseObject.methods_list){
                        $('#list-methods').append("<div id='"+responseObject.methods_list[method].method+"'></div>");
                        $('#'+responseObject.methods_list[method].method).append("<h3>" + responseObject.methods_list[method].method + "</h3>");
                        for(transport in responseObject.methods_list[method].transports){
                            $('#'+responseObject.methods_list[method].method).append("<p class='button "+responseObject.methods_list[method].transports[transport]+"' onclick='send_code(\""+responseObject.methods_list[method].transports[transport]+"\");'>" + responseObject.methods_list[method].transports[transport] + "</p>");
                        }
                    }
                } else {
                    alert('Erreur ' + responseObject.message);
                }
            }
        }
    };
    req.send(null);
};

function get_available_transports() {
    if (document.getElementById('username').value != '') {
        var req = new XMLHttpRequest();
        req.open('GET', 'https://tequila:3443/get_available_transports/'+document.getElementById('username').value, true);
        req.onerror = function(e) {
            console.log(e);
        };
        req.onreadystatechange = function(aEvt) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var responseObject = JSON.parse(req.responseText);
                    if (responseObject.code == "Ok") {
                        if(!responseObject.transports_list.sms){
                            $('.sms').remove();
                        }
                        if(!responseObject.transports_list.mail){
                            $('.mail').remove();
                        }
                        $('#list-methods').show();
                        var username = document.getElementById('username').value;
                        $('#username').remove();
                        $('#buttonMethods').remove();
                        $('#usernameRow').append("<label id='username'>" + username + "</label>");
                    }
                }
            }
        };
        req.send(null);
    } else alert("Veuillez entrer votre login");
};



function init() {
    $('#auth').hide();
    $('#list-methods').hide();
    get_available_methods();
};


// <div id="list-transports">
//   <div class="list-transports">
//   <h3>Veuillez choisir le moyen par lequel vous souhaitez recevoir votre code temporaire</h3>
//   Votre login <input type="text" id="loginUser" name="LastName">
//   <p id="buttonMail" onclick="send_code('mail')" class="button">Mail</p> <p id="buttonSms" onclick="send_code('sms')" class="button" >Sms</p> <p id="buttonApp" onclick="send_code('app')" class="button">Application smartphone</p>
// </div>
// </div>

//    <p onclick="$('#list-transports').show();" class="button">Renvoyer code</p>
