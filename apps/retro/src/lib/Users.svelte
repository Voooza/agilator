<script>
 import { createEventDispatcher } from 'svelte';
 import { onMount } from 'svelte';
 import { messageStore } from './store.js';
 import { sendMessage } from "./store.js";

 const dispatch = createEventDispatcher();
 let users = [];

 function mouseenter (event){
     dispatch ('mouseenter', {name: event.target.firstChild.textContent});
 }
 
 function mouseleave (event){
     dispatch ('mouseleave', {name: event.target.firstChild.textContent});
 }

 function click (event) {
     dispatch ('click', {name: event.target.firstChild.textContent});
 }
 
 messageStore.subscribe (value => {
     if (value) {
         users = value.users;
     }
 });
 
</script>

<div class="usr-wrp">
    Who's here:
    {#each users as user}
        <a class="usr-lbl" on:mouseenter={mouseenter} on:mouseleave={mouseleave} on:click={click}>{user}</a>
    {/each}
</div>

<style>
 .usr-wrp{
     /* position: absolute;
        bottom: 1em; */
     margin: 2em;
     color: #268bd2;
 }
 .usr-lbl {
     margin: 1.4em;
     color: #268bd2;
     cursor: pointer;
 }
</style>
