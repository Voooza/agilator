<script>
 import { onMount } from 'svelte';
 
 let url = "";
 
 onMount(() => url = window.location.href);
 
 let sessionId = "";
 let sessionUrl = "";
 

 function generate () {
     sessionId = self.crypto.randomUUID();
     sessionUrl = url + "/" + sessionId;
     window.location.href = sessionUrl;
 }
 
 if (sessionId) {
     sessionUrl = url + "?session=" + sessionId;
 }
</script>

{#if sessionUrl}
    <Board></Board>
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
     border: 1px dotted green;
     border-radius: 15px 7px;
     padding: 10px;
 }
 .btn {
     /* display: block; */
     width: 30%;
     margin: auto;
 }
 .inp {
     width: 67%;
 }
 
</style>
