package org.thinkit.data.gather;

import java.net.URI;
import java.util.List;

import org.thinkit.data.vo.FileAbstract;

public interface GatherInteface {
	List<FileAbstract> getFileAbstractlist(URI u); 
}