var code_send = false;
var last_transport = '';

function send_code(transport) {
    if (!code_send) {
        if (document.getElementById('username').value != '') {
            code_send = true;
            last_transport = transport;
            var req = new XMLHttpRequest();
            req.open('GET', 'https://tequila:3443/send_code/google_authenticator/' + transport + '/' + document.getElementById('username').value, true);
            req.onerror = function(e) {
                alert("Erreur :" + e.target.status);
            };
            req.onreadystatechange = function(aEvt) {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        var responseObject = JSON.parse(req.responseText);
                        if(responseObject.code =="Ok"){
                          alert('Envoi du code via ' + transport);
                        }else{
                          alert('Erreur ' + responseObject.message);
                        }
                        
                        code_send = false;
                    } else {
                        alert("Erreur " + req.status);
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

// <div id="list-transports">
//   <div class="list-transports">
//   <h3>Veuillez choisir le moyen par lequel vous souhaitez recevoir votre code temporaire</h3>
//   Votre login <input type="text" id="loginUser" name="LastName">
//   <p id="buttonMail" onclick="send_code('mail')" class="button">Mail</p> <p id="buttonSms" onclick="send_code('sms')" class="button" >Sms</p> <p id="buttonApp" onclick="send_code('app')" class="button">Application smartphone</p>
// </div>
// </div>

//    <p onclick="$('#list-transports').show();" class="button">Renvoyer code</p>