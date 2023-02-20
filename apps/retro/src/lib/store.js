import { writable } from "svelte/store";

const initialValue = window.localStorage.getItem("nick");

export const nick = writable (initialValue);

nick.subscribe(value => {
    if (value !== null) window.localStorage.setItem("nick", value);
});

export const messageStore = writable ("");

let socket;

var loc = window.location, wsuri, port;

wsuri = loc.protocol === "https:" ? "wss://" : "ws://";
wsuri += loc.hostname;
wsuri += loc.port === "5173" ? ":8080" : "";

socket = new WebSocket(wsuri);

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

