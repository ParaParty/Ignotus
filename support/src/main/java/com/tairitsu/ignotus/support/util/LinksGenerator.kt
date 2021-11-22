package com.tairitsu.ignotus.support.util

import com.tairitsu.ignotus.support.model.vo.Pagination
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class LinksGenerator {
    @Autowired
    private lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping;

    fun getBaseUrl(className: String, methodName: String): String {
        val map = requestMappingHandlerMapping.handlerMethods

        for (s in map) {
            val info: RequestMappingInfo = s.key;
            val method: HandlerMethod = s.value;

            if (method.method.declaringClass.name != className) continue;
            if (method.method.name != methodName) continue;

            info.patternsCondition?.patterns?.forEach { c -> return c };
        }

        return "";
    }

    fun generate(
        className: String, methodName: String,
        filter: Map<String, String>,
        include: Set<String>,
        sort: List<Pair<String, String>>,
        pagination: Pagination, count: Long,
    ): Map<String, String> {
        val baseUrl = getBaseUrl(className, methodName);
        val params = ArrayList<String>();

        filter.forEach { (k, v) -> params.add("filter%5B$k%5D=$v") };
        params.add(include.joinToString(",", "include="));
        params.add(sort.joinToString(",", "sort=") { s ->
            when (s.second) {
                "ASC" -> s.first;
                "DESC" -> "-${s.first}";
                else -> s.first;
            }
        });

        val ret = HashMap<String, String>();

        @Suppress("UNCHECKED_CAST")
        val firstParams = params.clone() as ArrayList<String>;
        firstParams.add("page%5Bnumber%5D=${1}")
        firstParams.add("page%5Bsize%5D=${pagination.limit}")
        ret.put("first", baseUrl + "?" + firstParams.joinToString("&"))

        if (pagination.number > 1) {
            @Suppress("UNCHECKED_CAST")
            val prevParams = params.clone() as ArrayList<String>;
            prevParams.add("page%5Bnumber%5D=${pagination.number - 1}")
            prevParams.add("page%5Bsize%5D=${pagination.limit}")
            ret.put("prev", baseUrl + "?" + prevParams.joinToString("&"))
        }

        @Suppress("UNCHECKED_CAST")
        val selfParams = params.clone() as ArrayList<String>;
        selfParams.add("page%5Bnumber%5D=${pagination.number}")
        selfParams.add("page%5Bsize%5D=${pagination.limit}")
        ret.put("self", baseUrl + "?" + selfParams.joinToString("&"))

        val lastPageNumber =
            if (count % pagination.limit > 0)
                count / pagination.limit + 1
            else
                count / pagination.limit

        if (pagination.number < lastPageNumber) {
            @Suppress("UNCHECKED_CAST")
            val nextParams = params.clone() as ArrayList<String>;
            nextParams.add("page%5Bnumber%5D=${pagination.number + 1}")
            nextParams.add("page%5Bsize%5D=${pagination.limit}")
            ret.put("next", baseUrl + "?" + nextParams.joinToString("&"))
        }

        @Suppress("UNCHECKED_CAST")
        val lastParams = params.clone() as ArrayList<String>;
        lastParams.add("page%5Bnumber%5D=${lastPageNumber}")
        lastParams.add("page%5Bsize%5D=${pagination.limit}")
        ret.put("last", baseUrl + "?" + lastParams.joinToString("&"))

        return ret;
    }

}
