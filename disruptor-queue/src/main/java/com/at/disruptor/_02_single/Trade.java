package com.at.disruptor._02_single;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Disruptor中的 Event
 *
 * @author Alienware
 */
public class Trade {

    private String id;
    private String name;
    private double price;
    private AtomicInteger count = new AtomicInteger(0);

    public Trade() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Trade trade = (Trade) o;
		return Double.compare(trade.price, price) == 0 && Objects.equals(id, trade.id) && Objects.equals(name, trade.name) && Objects.equals(count, trade.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, price, count);
	}

	@Override
	public String toString() {
		return "Trade{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", price=" + price +
				", count=" + count +
				'}';
	}
}
