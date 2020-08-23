package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 前缀数，过滤敏感词
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 加载敏感词，初始化前缀树
     */
    @PostConstruct
    public void init(){
        try(
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词失败: "+ e.getMessage());
        }
    }

    /**
     * 将敏感词添加到前缀树
     * @param keyword 敏感词
     */
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            //子节点还没有
            if (subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //子节点已经有了,进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        //结果
        StringBuilder stringBuilder = new StringBuilder();

        while (position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    stringBuilder.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                stringBuilder.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;
            }
            else if(tempNode.isKeywordEnd()){
                //发现敏感词,以begin开头，position结尾
                stringBuilder.append(REPLACEMENT);
                position++;
                begin = position;
                tempNode = rootNode;
            }
            else{
                position++;
            }
        }
        //将最后一批字符计入结果
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
   }

   //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c>0x9fff);
    }

    private class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd = false;
        //子节点(key是下级字符，value是下级节点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
