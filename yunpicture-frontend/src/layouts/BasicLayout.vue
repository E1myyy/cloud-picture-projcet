<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header"><GlobalHeader /></a-layout-header>
      <a-layout-content class="content">
        <router-view/>
      </a-layout-content>
      <a-layout-footer class="footer">
        by E1my
        <a href="https://github.com/E1myyy" target="_blank" rel="noopener noreferrer">github主页</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basicLayout .header {
  background: white;
  color: unset;
  margin-bottom: 0;
}
#basicLayout .content {
  padding: 20px;
  background: linear-gradient(to right,#efefef, #fff);
  nargin-bottom: 20px;
}
#basicLayout .footer {
  background-color: #efefef;
  padding: 15px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}


</style>
