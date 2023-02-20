<script>
 import { createEventDispatcher } from 'svelte';
 import { messageStore } from './store.js';
 
 export let placeholder = '';
 export let color = "";
 export let bg_color = "";
 export let category = "";

 const dispatch = createEventDispatcher();
 
 let remarks = [];
 let highlightUsr = "";
 
 function add(input) {
     if (input.value.length >= 3){
         dispatch ('message', {text: input.value, cat: category});
         input.value = "";
     }
 }

 export function setHighlightUser (user){
     highlightUsr = user;
 }
 
 messageStore.subscribe (value => {
     if (value) {
         remarks = value.remarks.filter (r => r.cat === category);
     }
 });

</script>

<div class='board'>
    <input
        placeholder={placeholder}
        on:keydown={e => e.key === 'Enter' && add(e.target)}
    >
    <div>
        {#each remarks as remark}
            <label style="background-color: {bg_color};
                          color: {color}; 
                          border: 1px solid {color};
                          opacity: {highlightUsr.length > 0 && highlightUsr !== remark.owner ? 0.2 :  1};">
                {remark.content}
                <!-- <button on:click="{() => remove(remark)}">remove</button> -->
            </label>
        {/each}
    </div>
</div>

<style>
 .board {
     margin: 0 auto;
     min-width: 15em;
 }
 
 .board > input {
     font-size: 1.4em;
     /* display: block; */
     margin: 0 0 0.5em 0;
     padding: 0.5em 0.2em 0.5em 0.2em;
     line-height: 1.2;
     min-width: 15em;
     border-radius: 15px 7px;

 }

 label {
     position: relative;
     line-height: 1.2;
     font-size: 1.4em;
     padding: 0.5em 0.9em 0.5em 0.5em;
     margin: 0 0 0.5em 0;
     user-select: none;
     border-radius: 15px 7px;
     display: block;
     max-width: 14em;
     word-wrap: break-word;
 }

 button {
     position: absolute;
     top: 0;
     right: 0.2em;
     width: 2em;
     height: 100%;
     background: no-repeat 50% 50% url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%23676778' d='M12,2C17.53,2 22,6.47 22,12C22,17.53 17.53,22 12,22C6.47,22 2,17.53 2,12C2,6.47 6.47,2 12,2M17,7H14.5L13.5,6H10.5L9.5,7H7V9H17V7M9,18H15A1,1 0 0,0 16,17V10H8V17A1,1 0 0,0 9,18Z'%3E%3C/path%3E%3C/svg%3E");
     background-size: 1.4em 1.4em;
     border: none;
     opacity: 0;
     transition: opacity 0.2s;
     text-indent: -9999px;
     cursor: pointer;
 }
 
 label:hover button {
     opacity: 1;
 }

</style>
