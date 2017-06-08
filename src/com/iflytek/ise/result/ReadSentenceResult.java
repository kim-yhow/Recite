/**
 * 
 */
package com.iflytek.ise.result;

import com.iflytek.ise.result.util.ResultFormatUtil;

/**
 * <p>Title: ReadSentenceResult</p>
 * <p>Description: </p>
 * <p>Company: www.iflytek.com</p>
 * @author iflytek
 * @date 2015年1月12日 下午5:04:14
 */
public class ReadSentenceResult extends Result {
	
	public ReadSentenceResult() {
		category = "read_sentence";
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		String tempStr=String.valueOf(total_score*20);
		for(int i=0;i<tempStr.length();i++){
			if(tempStr.charAt(i)=='.'){
				if(tempStr.length()-i==2) tempStr+='0';
				else tempStr=tempStr.substring(0,i+2);
			}
		}
		
		if ("cn".equals(language)) {
			buffer.append("[总体结果]\n")
				.append("背诵内容："+"\n"+ content)
				.append("背诵时长：" + (double)Integer.valueOf(time_len)/100 +"s"+ "\n")
				.append("总分：" +  tempStr+ "\n\n")
				.append("[背诵详情]").append(ResultFormatUtil.formatDetails_CN(sentences));
		} else {
			if (is_rejected) {
				buffer.append("检测到乱读，")
					.append("except_info:" + except_info + "\n\n");	// except_info代码说明详见《语音评测参数、结果说明文档》
			}
			
			buffer.append("[总体结果]\n")
				.append("评测内容：" + content + "\n")
				.append("总分：" + total_score + "\n\n")
				.append("[朗读详情]").append(ResultFormatUtil.formatDetails_EN(sentences));
		}
		
		return buffer.toString();
	}
}
