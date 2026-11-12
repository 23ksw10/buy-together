package com.together.buytogether.common.lock;

public class LockNumberConstants {
	public static final int LOCK_WAIT_SECONDS = 3;

	public static final long LOCK_WAIT_MILLI_SECOND = LOCK_WAIT_SECONDS * 1000L;

	public static final int LOCKED_TX_TIMEOUT_SECONDS = LOCK_WAIT_SECONDS;
}
