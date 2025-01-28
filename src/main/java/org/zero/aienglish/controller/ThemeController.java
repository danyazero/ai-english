package org.zero.aienglish.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.ThemeOuterClass;
import org.zero.aienglish.lib.grpc.ThemeServiceGrpc;
import org.zero.aienglish.model.ThemeDTO;
import org.zero.aienglish.service.ThemeService;

import java.util.function.Function;

@GrpcService
@RequiredArgsConstructor
public class ThemeController extends ThemeServiceGrpc.ThemeServiceImplBase {
    private final ThemeService themeService;

    @Override
    public void selectTheme(ThemeOuterClass.SelectThemeRequest request, StreamObserver<Empty> responseObserver) {
        var userId = request.getUserId();
        var themeId = request.getThemeId();

        themeService.selectTheme(userId, themeId);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void clearThemes(ThemeOuterClass.ClearThemeRequest request, StreamObserver<Empty> responseObserver) {
        var userId = request.getUserId();

        themeService.clearTheme(userId);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getCategories(Empty request, StreamObserver<ThemeOuterClass.CategoriesResponse> responseObserver) {
        var themeCategories = themeService.getThemeCategories();

        var themes = themeCategories.stream()
                .map(mapToGrpcTheme())
                .toList();

        var categoriesResponse = ThemeOuterClass.CategoriesResponse.newBuilder()
                .addAllThemes(themes)
                .build();

        responseObserver.onNext(categoriesResponse);
        responseObserver.onCompleted();
    }

    private static Function<ThemeDTO, ThemeOuterClass.Theme> mapToGrpcTheme() {
        return element -> ThemeOuterClass.Theme.newBuilder()
                .setId(element.id())
                .setTitle(element.title())
                .build();
    }

    @Override
    public void getThemes(ThemeOuterClass.ThemesRequest request, StreamObserver<ThemeOuterClass.ThemesResponse> responseObserver) {
        var categoryId = request.getCategoryId();
        var page = request.getPage();

        var themePage = themeService.getThemeForCategory(categoryId, page);

        var themes = themePage.items().stream()
                .map(mapToGrpcTheme())
                .toList();

        var pageResponse = ThemeOuterClass.ThemesResponse.newBuilder()
                .setCurrentPage(themePage.currentPage())
                .setTotalPages(themePage.totalPages())
                .addAllThemes(themes)
                .build();

        responseObserver.onNext(pageResponse);
        responseObserver.onCompleted();
    }
}
