<script>
 import { onMount } from 'svelte';
 import Board from './lib/Board.svelte';
 import Header from './Header.svelte';
 
 let sessionId = "";
 
 function parseSessionId (url){
     let comps = url.split ("/");
     let lastComp = comps [comps.length - 1];
     if (lastComp !== '' && lastComp !== 'retro'){
         sessionId = lastComp;
     }
 }

 function generate () {
     sessionId = self.crypto.randomUUID();
     let sessionUrl = window.location.href + sessionId;
     window.location.href = sessionUrl;
 }
 
 parseSessionId (window.location.href);
 
</script>
<Header></Header>

{#if sessionId.length > 0}
    <Board session={sessionId}></Board>
{:else}
    <div class="form">
        <button class="btn" on:click={generate}>New sessison</button>
    </div>
{/if}

<style>
 .form {
     display: block;
     margin: 10em auto;
     width: 50%;
     border-radius: 15px 7px;
     padding: 10px;
 }
 .btn {
     width: 100%;
     height: 3em;
     font-size: 1.4em;
     margin: auto;
     border-radius: 15px 7px;
 }
 .inp {
     width: 67%;
 }
 
</style>


