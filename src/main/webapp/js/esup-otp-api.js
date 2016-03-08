var code_send = false;
var last_transport = '';

function send_code(transport) {
    if (!code_send) {
        if (document.getElementById('loginUser').value != '') {
            code_send = true;
            last_transport = transport;
            var req = new XMLHttpRequest();
            req.open('GET', 'https://tequila:3443/send_code/google_authenticator/' + transport + '/' + document.getElementById('loginUser').value, true);
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
