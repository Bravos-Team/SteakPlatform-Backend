package com.bravos.steak.common.model;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ConcurrentTrieNode {

    private final Map<Character,ConcurrentTrieNode> children = new ConcurrentHashMap<>();

}
