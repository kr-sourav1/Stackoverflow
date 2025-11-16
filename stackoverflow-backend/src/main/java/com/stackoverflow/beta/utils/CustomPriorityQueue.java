package com.stackoverflow.beta.utils;

import com.stackoverflow.beta.model.Question;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

public final class CustomPriorityQueue extends PriorityQueue<Question> {

    private static final int MAX_SIZE = 100;
    //A volatile field ensures visibility of updates across threads.
    private static volatile CustomPriorityQueue instance;
    private static final ReentrantLock lock = new ReentrantLock();

    private CustomPriorityQueue() {
        super(Comparator.comparingInt(Question::getVotes).reversed());
    }

    public static CustomPriorityQueue getInstance() {
        if (instance == null) {
            synchronized (CustomPriorityQueue.class) {
                if (instance == null) {
                    instance = new CustomPriorityQueue();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean add(Question question) {
        lock.lock();
        try {
            boolean result = super.add(question);
            while (size() > MAX_SIZE) {
                poll();
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends Question> questions) {
        lock.lock();
        try {
            return questions.stream()
                    .map(this::add)
                    .reduce(false, Boolean::logicalOr);
        } finally {
            lock.unlock();
        }
    }
    public boolean containsQuestion(Question question) {
        lock.lock();
        try {
            return this.stream().anyMatch(q -> q.getId() == question.getId());
        } finally {
            lock.unlock();
        }
    }

    public void updateQuestion(Question question) {
        lock.lock();
        try {
            // Remove the existing question with the same ID, if any
            boolean removed = this.removeIf(q -> q.getId() == question.getId());
            if (removed) {
                this.add(question);
            }
        } finally {
            lock.unlock();
        }
    }
}
