<script>
 import { onMount } from 'svelte';
 import Board from './lib/Board.svelte';
 import Header from './Header.svelte';
 
 let sessionId = new URLSearchParams (window.location.search).get ("session");
 
 function generate () {
     sessionId = self.crypto.randomUUID();
     let sessionUrl = window.location.href + "?session=" + sessionId;
     window.location.href = sessionUrl;
 }
 
 </script>
<Header></Header>

{#if sessionId != null}
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


