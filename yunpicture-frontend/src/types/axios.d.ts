// src/types/axios.d.ts
import 'axios';

declare module 'axios' {
  export interface AxiosRequestConfig {
    // 添加自定义属性
    requestType?: 'form' | 'json' | 'text';
  }
}
