import { writable } from "svelte/store";

const initialValue = window.localStorage.getItem("nick");

export const nick = writable (initialValue);

nick.subscribe(value => {
    window.localStorage.setItem("nick", value);
});

export const messageStore = writable ("");

let socket;

var loc = window.location, new_uri, port;
if (loc.protocol === "https:") {
        new_uri = "wss:";
    } else {
        new_uri = "ws:";
    }

new_uri += "//" + loc.hostname;

socket = new WebSocket(new_uri);

socket.addEventListener('open', function (event) {
        console.log("It's open");
    });

socket.addEventListener('message', function (event) {
        console.log (event.data);
        let data = JSON.parse(event.data);
        if (!data.infra){
            messageStore.set(data);
        }
    });



export const sendMessage = (message) => {
    if (socket.readyState <= 1) {
	socket.send(message);
    }
}

