package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * 测试多线程
 */
public class ThreadTest {
	//设置线程池数量
	public static ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main...start...");
//		System.out.println("主线程：" + Thread.currentThread().getId());
//		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//			System.out.println("当前线程：" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结果：" + i);
//		}, executor);

//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程：" + Thread.currentThread().getId());
//			int i = 10 / 0;
//			System.out.println("运行结果：" + i);
//			return i;
//		}, executor).whenComplete((res, exception) -> {
//			System.out.println("任务执行完成了。。。结果是：" + res + "; 异常是：" + exception);
//		}).exceptionally(throwable -> {
//			// 如果出现异常了，可以自定义返回值
//			return 10;
//		});

//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程：" + Thread.currentThread().getId());
//			int i = 10 / 0;
//			System.out.println("运行结果：" + i);
//			return i;
//		}, executor).handle((res, exception) -> {
//			//任务执行完成后的处理，可以返回新任务的执行结果
//			if (res != null) {
//				return res * 2;
//			}
//			if (exception != null) {
//				return -1;
//			}
//			return 0;
//		});
//		Integer integer = future.get();

		/**
		 * 线程串行化
		 */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("运行结果：" + i);
//			return i;
//		}, executor).thenRunAsync(() -> {
//			System.out.println("任务2执行。。。");
//		}, executor); //无法感知前一个任务的返回值，自身也没有返回值

//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("运行结果：" + i);
//			return i;
//		}, executor).thenRun(() -> {
//			System.out.println("任务2执行。。。线程："+ Thread.currentThread().getId());
//		}); //无法感知前一个任务的返回值，自身也没有返回值

//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("任务1运行结果：" + i);
//			return i;
//		}, executor).thenAcceptAsync(res->{
//			System.out.println("前一任务的返回值："+res);
//			System.out.println("任务2执行。。。");
//		},executor);//可以感知前一个任务的返回值，自身没有返回值
		//void accept(T t);

//		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("任务1运行结果：" + i);
//			return i;
//		}, executor).thenApplyAsync(res -> {
//			System.out.println("前一任务的返回值：" + res);
//			String val = "hello task2";
//			System.out.println("任务2执行。。。结果：" + val);
//			return val;
//		}, executor);
//		//R apply(T t);
//		String task2Res = future.get();

		/**
		 * 两任务执行-都要完成
		 */
//		CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1开始。。。线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("任务1运行结果：" + i);
//			return i;
//		}, executor);
//		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务2开始。。。线程：" + Thread.currentThread().getId());
//			String val = "hello";
//			System.out.println("任务2运行结果：" + val);
//			return val;
//		}, executor);

		// void run() 无法感知前两个任务的返回值，自身也没有返回值
//		CompletableFuture<Void> future3 = future1.runAfterBothAsync(future2, () -> {
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//		}, executor);

		//不带async是在主线程中执行
//		System.out.println("主线程：" + Thread.currentThread().getId());
//		CompletableFuture<Void> future3 = future1.runAfterBoth(future2, () -> {
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//		});

		//void accept(T t, U u); 可以感知前两个任务的返回值，自身没有返回值
//		CompletableFuture<Void> future3 = future1.thenAcceptBothAsync(future2, (f1, f2) -> {
//			System.out.println("任务1结果：" + f1 + ", 任务2结果：" + f2);
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//		}, executor);

		//R apply(T t, U u); 可以感知前两个任务的返回值，自身也有返回值
//		CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (f1, f2) -> {
//			System.out.println("任务1结果：" + f1 + ", 任务2结果：" + f2);
//			String val = "task3";
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//			System.out.println("任务3结果：" + val);
//			return val;
//		}, executor);
//		String val = future3.get();

		/**
		 * 两任务执行-只要有一个完成，就执行第三个任务
		 */
//		CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1开始。。。线程：" + Thread.currentThread().getId());
//			int i = 10 / 4;
//			System.out.println("任务1运行结果：" + i);
//			return i;
//		}, executor);
//		CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务2开始。。。线程：" + Thread.currentThread().getId());
//			String val = "hello";
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println("任务2运行结果：" + val);
//			return val;
//		}, executor);

		// 无法感知前面优先完成任务的返回值，自身也没有返回值
//		future1.runAfterEitherAsync(future2,()->{
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//		},executor);

		// 可以感知前面优先完成任务的返回值，自身没有返回值。
		// 注意点：前面两个任务的返回值需要是同种类型，否则无法确定入参的类型
//		future1.acceptEitherAsync(future2, res -> {
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//			System.out.println("前面任务的返回值：" + res);
//		}, executor);

		// R apply(T t);
//		CompletableFuture<String> future3 = future1.applyToEitherAsync(future2, res -> {
//			System.out.println("前面任务的结果：" + res);
//			String val = "task3";
//			System.out.println("任务3执行。。。线程：" + Thread.currentThread().getId());
//			System.out.println("任务3结果：" + val);
//			return val;
//		}, executor);
//		String task3Res = future3.get();

		/**
		 * 多任务组合
		 */
		CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
			System.out.println("查询商品图片");
			return "hello.jpg";
		}, executor);
		CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(3000);
				System.out.println("查询商品属性");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "黑色+256G";
		}, executor);
		CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {
			System.out.println("查询商品介绍");
			return "华为";
		}, executor);

		// 三个都执行完成了获取结果
//		CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02, future03);
//		future.get();
//		System.out.println("main...end..." + future01.get() + "=>" + future02.get() + "=>" + future03.get());

		CompletableFuture<Object> future = CompletableFuture.anyOf(future01, future02, future03);
		future.get();
		System.out.println("main...end..." + future.get());
	}

	public void thread(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main...start...");

		/**
		 * 实现多线程的四种方式
		 * 1、继承Thread
		 * 	new Thread01().start();//启动线程
		 * 2、实现Runnable
		 *  方式一：
		 * 	  Runnable01 runnable = new Runnable01();
		 *    new Thread(runnable).start();
		 *	方式二：
		 * 	  //使用lambda表达式传入Runnable
		 * 	  new Thread(() -> System.out.println("线程输出")).start();
		 * 3、实现Callable+FutureTask，可以拿到拿到返回结果，可以处理异常
		 * 4、线程池
		 *
		 * 开启线程时，都需要使用new Thread().start()来开启
		 *
		 * 四种方式的区别：
		 * 	1、2不能得到返回值，3可以得到返回值
		 * 	1、2、3不能控制资源
		 * 	4可以控制资源，性能稳定
		 */
		//FutureTask里层是继承了Runnable
//		FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//		new Thread(futureTask).start();//开启线程
//
//		//阻塞等待线程执行完成，获取返回的结果
//		Integer result = futureTask.get();
//		System.out.println("main...end..." + result);

		//以上三种方式都无法控制资源，每次创建都需要创建一个Thread对象，易造成服务器的不稳定。
		//在以后的业务代码中，都将所有多线程异步任务交给线程池执行，由线程池统一管理线程的创建和销毁，避免同一时间过多的线程同时创建影响性能
		//一个服务里面只会有一个线程池对象
		executor.execute(new Runnable01());

		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				5,
				10,
				10,
				TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(100000),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());

		System.out.println("main...end...");
	}

	public static class Thread01 extends Thread {
		@Override
		public void run() {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
		}
	}

	public static class Runnable01 implements Runnable {
		@Override
		public void run() {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
		}
	}

	public static class Callable01 implements Callable<Integer> {
		//可以有返回值
		@Override
		public Integer call() throws Exception {
			System.out.println("当前线程：" + Thread.currentThread().getId());
			int i = 10 / 2;
			System.out.println("运行结果：" + i);
			return i;
		}
	}

}
