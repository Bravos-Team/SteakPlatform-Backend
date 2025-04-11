package com.bravos.steak.common.model;

import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentTrie {

    private final ConcurrentTrieNode root = new ConcurrentTrieNode();

    private final Set<String> words = ConcurrentHashMap.newKeySet();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    public ConcurrentTrie() {}

    /**
     *
     * @param words Set từ khóa
     */
    public ConcurrentTrie(Collection<String> words) {
        this.words.addAll(words);
        words.forEach(word -> {
            ConcurrentTrieNode current = root;
            for (char ch : word.toCharArray()) {
                current = current.getChildren().computeIfAbsent(ch, c -> new ConcurrentTrieNode());
            }
        });
    }

    /**
     * Chèn dữ liệu vào từ điển
     *
     * @param word từ cần bỏ vào
     */
    public void insert(@NonNull String word) {
        writeLock.lock();
        try {
            if (words.add(word)) {
                ConcurrentTrieNode current = root;
                for (char ch : word.toCharArray()) {
                    current = current.getChildren().computeIfAbsent(ch, c -> new ConcurrentTrieNode());
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Kiểm tra tiền tố có match ko
     *
     * @param prefix tiền tố cần kiểm
     * @return true: có
     */
    public boolean startsWith(@NonNull String prefix) {
        readLock.lock();
        try {
            ConcurrentTrieNode current = root;
            for (char ch : prefix.toCharArray()) {
                current = current.getChildren().get(ch);
                if (current == null) return false;
            }
            return true;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Search coi có không ?
     *
     * @param word từ cần search
     * @return true: có
     */
    public boolean search(@NonNull String word) {
        readLock.lock();
        try {
            return words.contains(word);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Đếm số lượng từ
     * @return số lượng từ
     */
    public long getWordsCount() {
        readLock.lock();
        try {
            return words.size();
        } finally {
            readLock.unlock();
        }
    }

}
