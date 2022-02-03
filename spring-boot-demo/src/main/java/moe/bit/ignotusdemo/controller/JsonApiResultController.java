package moe.bit.ignotusdemo.controller;

import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import com.tairitsu.ignotus.serializer.vo.RootResponse;
import moe.bit.ignotusdemo.model.vo.AuthorVo;
import moe.bit.ignotusdemo.model.vo.BookVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class JsonApiResultController {
    @GetMapping("api/resource")
    @JsonApiController()
    public RootResponse object() {
        BookVo data = new BookVo("深入理解计算机系统");
        data.setLink("purchase", "https://item.jd.com/12006637.html");
        data.setMeta("alias", "没人理解计算机系统");

        AuthorVo cmpBook = new AuthorVo("机械工业出版社");
        cmpBook.setLink("offical_website", "http://cmpbook.com/");
        data.setRelationship("author", cmpBook);


        List<BookVo> relatedBooks = new ArrayList<>();

        BookVo bookCompile = new BookVo("编译原理");
        bookCompile.setMeta("alias", "龙书");
        relatedBooks.add(bookCompile);

        relatedBooks.add(new BookVo("计算机网络"));
        relatedBooks.add(new BookVo("计算机组成原理"));
        cmpBook.setRelationship("books", relatedBooks);

        RootResponse ret = new RootResponse();
        ret.setData(data);
        ret.setLink("self", "/api/resource");
        return ret;
    }


    @GetMapping("api/resources")
    @JsonApiController()
    public RootResponse list() {
        List<BookVo> relatedBooks = new ArrayList<>();

        BookVo bookCompile = new BookVo("编译原理");
        bookCompile.setMeta("alias", "龙书");
        relatedBooks.add(bookCompile);

        relatedBooks.add(new BookVo("计算机网络"));
        relatedBooks.add(new BookVo("计算机组成原理"));

        RootResponse ret = new RootResponse();
        ret.setData(relatedBooks);
        ret.setLink("self", "/api/resources");
        return ret;
    }
}
