'use client'
import Image from "next/image";
import { useRef, useState } from 'react';
import { createWorker, PSM, createScheduler } from 'tesseract.js';
// import cv from 'opencv.js'
import styles from "./page.module.css";

export default function Home() {
  // method one : use 阿里api.
  const queryData = async(_data) => {
    try {
      const data = await fetch('https://dashscope.aliyuncs.com/api/v1/apps/5ec6f795a4884adfa3bbb7f8f860dc96/completion', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer sk-7d51bdd255db481eb9367dc503ef5a17',
            'Content-Type': 'application/json'
          },
        body: JSON.stringify({
            input: {
              prompt: _data,
            }
          })
      })

      const result = await data.json(); 
      console.log('=========>result', result);
      console.log('======>text json', JSON.parse(result.output.text) )

    } catch(e) {
      console.log('===========>e', e);
    }

  }

  // method two: use tesseract.js
  const [text, setText] = useState<string>('');
  const [progress, setProgress] = useState<number>(0);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const canvasRef = useRef(null);


  // 图片预处理
//  async function lightweightProcess (file: File) {
//   const img = await createImageBitmap(file);

//   const canvas = document.createElement('canvas');
//   const ctx = canvas.getContext('2d');

//   if(!ctx) {throw new Error('无法获取canvas上下文')};
//   canvas.width = img.width;
//   canvas.height = img.height;

//   ctx.drawImage(img, 0, 0);
//   const imageData = ctx.getImageData(0,0, canvas.width, canvas.height);
//   const data = imageData.data;
//   // 简单灰度化和对比度增强
//   for(let i = 0; i < data.length; i += 4) {
//     // 灰度化
//     const r = data[i];
//     const g = data[i + 1];
//     const b = data[i + 2];
//     const gray = 0.299 *  r + 0.578 * g + 0.114 * b;
//     // const gray = (r + g + b) / 3;
//     // 对比度增强
//     // const contrast = 1.5;
//     // const adjusted = Math.min(255, Math.max(0, (gray - 128) * contrast + 128));
//     //二值化(简单阈值)
//     // const threshold = 150;
//     // const binary = adjusted > threshold ? 255 : 0;

//     data[i] = data[i + 1] = data[i + 2] = gray;
//     // data[i + 3] = 255; // Alpha通道f
//   }
//   ctx.putImageData(imageData, 0, 0);
//   return canvas.toDataURL('image/jpeg');
// }

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) { return; };
    setIsLoading(true);
    setProgress(0);
    try {
      // const processeUrl = await lightweightProcess(file);
      const scheduler = createScheduler();
      const worker1 = await createWorker('chi_sim');
      const worker2 = await createWorker('chi_sim');
      scheduler.addWorker(worker1);
      scheduler.addWorker(worker2);
      await worker1.setParameters({
        tessedit_pageseg_mode: PSM.AUTO_OSD,
        // tessedit_char_whitelist: '1234567890/-:.年月日',
      })
      const [data1, data2] = await Promise.all([scheduler.addJob('recognize', file), scheduler.addJob('recognize', file)]);
      const data = `${data1.data.text} ${data2.data.text}`;
      debugger
      setText(data);
      console.log('=========>data', data);
      const result = await queryData(data);
      debugger
      await scheduler.terminate();
    } catch(error) {
      console.log('OCR Error: ', error);
    } finally {
      setIsLoading(false);
    }
  }


  return (
    <div className={styles.page}>
      <h1>OCR with Tesseract.js</h1>
      {/* <button onClick={queryData}>get data</button> */}
      <input 
        type="file"
        accept="image/*"
        onChange={handleImageUpload}
        disabled={isLoading}
      />

      {isLoading && (
        <div>
          <progress value={progress} max='100' />
          <span>{progress}%</span>
        </div>
      )}
      {text && (
        <div>
          <h2>识别结果</h2>
          <pre>{text}</pre>
        </div>
      )}
      <canvas ref={canvasRef} style={{ display: 'none' }}/>
    </div>
  );
}