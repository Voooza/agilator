import { writable } from "svelte/store";
import { browser } from "$app/environment";

const defaultValue = "";
const initialValue = browser ? window.localStorage.getItem("nick") ?? defaultValue : defaultValue;

export const nick = writable (initialValue);

nick.subscribe(value => {
    if (browser){
        window.localStorage.setItem("nick", value);
    }
});

export const messageStore = writable ("");

let socket;



if (browser){
    
    var loc = window.location, new_uri, port;
    if (loc.protocol === "https:") {
        new_uri = "wss:";
    } else {
        new_uri = "ws:";
    }
    if (loc.port === "5173") {
        port = 8080;
    } else {
        port = loc.port;
    }

    new_uri += "//" + loc.hostname + ":" + port;

    socket = new WebSocket("ws://agilator.cz:8585");

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

}

export const sendMessage = (message) => {
    if (browser && socket.readyState <= 1) {
	socket.send(message);
    }
}

