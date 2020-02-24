package com.luckypeng.study.pool2.hello;


import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author coalchan
 */
public class MyPooledObjectFactory implements PooledObjectFactory<MyConnection> {

    /**
     * 根据自己的业务创建和管理要对象池化的对象
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<MyConnection> makeObject() throws Exception {
        MyConnection myConnection = new MyConnection();
        return new DefaultPooledObject<>(myConnection);
    }

    /**
     * 销毁对象，当对象池检测到某个对象的空闲时间(idle)超时，或使用完对象归还到对象池之前被检测到对象已经无效时，就会调用这个方法销毁对象。
     * 对象的销毁一般和业务相关，但必须明确的是，当调用这个方法之后，对象的生命周期必须结果。
     * 如果是对象是线程，线程必须已结束，如果是socket，socket必须已close，如果是文件操作，文件数据必须已flush，且文件正常关闭。
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<MyConnection> p) throws Exception {
        p.getObject().close();
    }

    /**
     * 检测一个对象是否有效。在对象池中的对象必须是有效的，这个有效的概念是，从对象池中拿出的对象是可用的。
     * 比如，如果是socket,那么必须保证socket是连接可用的。
     * 在从对象池获取对象或归还对象到对象池时，会调用这个方法，判断对象是否有效，如果无效就会销毁。
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<MyConnection> p) {
        return p.getObject().isOpen();
    }

    /**
     * 激活一个对象或者说启动对象的某些操作。比如，如果对象是socket，如果socket没有连接，或意外断开了，可以在这里启动socket的连接。
     * 它会在检测空闲对象的时候，如果设置了测试空闲对象是否可以用，就会调用这个方法，在borrowObject的时候也会调用。
     * 另外，如果对象是一个包含参数的对象，可以在这里进行初始化。让使用者感觉这是一个新创建的对象一样。
     *
     * 一般来说 activateObject 和 passivateObject 是成对出现的。
     * 前者是在对象从对象池取出时做一些操作，后者是在对象归还到对象池做一些操作，可以根据自己的业务需要进行取舍。
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<MyConnection> p) throws Exception {

    }

    /**
     * 钝化一个对象。在向对象池归还一个对象是会调用这个方法。
     * 这里可以对对象做一些清理操作。比如清理掉过期的数据，下次获得对象时，不受旧数据的影响。
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<MyConnection> p) throws Exception {

    }
}
