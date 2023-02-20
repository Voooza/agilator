<script>
 function handleLinkClick (event){
     console.log (event);
     window.location.href = event.target.href;
 }
</script>
                                 
<nav class="anav">
<a href="/" rel="external">home</a>
|
<a href="/retro/">retro</a>
|
<a href="/poker/" on:click={handleLinkClick}>poker</a>
</nav>

<style>
 .anav {
     color: #268bd2;
     font-size: 1.4em;
     margin: 1em auto 2em auto;
     display: flex;
     justify-content: center
 }
 .anav>a {
     color: #268bd2;
     margin: 0 1em;
     text-decoration: none;
     /* font-size: 1.4em; */

     
 }
</style>
