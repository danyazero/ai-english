package org.zero.aienglish.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.zero.aienglish.lib.grpc.ThemeOuterClass;
import org.zero.aienglish.lib.grpc.ThemeServiceGrpc;
import org.zero.aienglish.mapper.ThemeMapper;
import org.zero.aienglish.model.Themes;
import org.zero.aienglish.service.ThemeService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class ThemeController extends ThemeServiceGrpc.ThemeServiceImplBase {
    private final ThemeService themeService;

    @Override
    public void selectTheme(ThemeOuterClass.SelectThemeRequest request, StreamObserver<ThemeOuterClass.SelectThemeResponse> responseObserver) {
        var userId = request.getUserId();
        var themeId = request.getThemeId();

        var relyThemes = themeService.selectTheme(userId, themeId);

        var mappedRecommendedThemes = relyThemes.recommendations().items().stream()
                .map(ThemeMapper::map)
                .toList();

        var themesResponse = ThemeOuterClass.ThemesResponse.newBuilder()
                .setCurrentPage(relyThemes.recommendations().currentPage())
                .setTotalPages(relyThemes.recommendations().totalPages())
                .addAllThemes(mappedRecommendedThemes)
                .build();


        var mappedSavedThemes = relyThemes.saved().stream()
                .map(ThemeMapper::map)
                .toList();

        var selectThemeResponse = ThemeOuterClass.SelectThemeResponse.newBuilder()
                .setThemes(themesResponse)
                .addAllSaved(mappedSavedThemes)
                .build();

        responseObserver.onNext(selectThemeResponse);
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
    public void getCategories(ThemeOuterClass.CategoriesRequest request, StreamObserver<ThemeOuterClass.CategoriesResponse> responseObserver) {
        var userId = request.getUserId();

        var themeCategories = themeService.getThemeCategories(userId);

        var themes = themeCategories.recommendations().items().stream()
                .map(ThemeMapper::map)
                .toList();

        var themesResponse = ThemeOuterClass.ThemesResponse.newBuilder()
                .setCurrentPage(themeCategories.recommendations().currentPage())
                .setTotalPages(themeCategories.recommendations().totalPages())
                .addAllThemes(themes)
                .build();

        var savedThemes = getSavedThemes(themeCategories);

        var categoriesResponse = ThemeOuterClass.CategoriesResponse.newBuilder()
                .addAllSaved(savedThemes)
                .setThemes(themesResponse)
                .build();

        responseObserver.onNext(categoriesResponse);
        responseObserver.onCompleted();
    }

    private static List<ThemeOuterClass.Theme> getSavedThemes(Themes themeCategories) {
          if (themeCategories.saved() != null) {
              return themeCategories.saved().stream()
                      .map(ThemeMapper::map)
                      .toList();
          }

          return List.of();
    }

    @Override
    public void getThemes(ThemeOuterClass.ThemesRequest request, StreamObserver<ThemeOuterClass.ThemesResponse> responseObserver) {
        var categoryId = request.getCategoryId();
        var page = request.getPage();
        var userId = request.getUserId();

        var themePage = themeService.getThemeForCategory(userId, categoryId, page);

        var themes = themePage.items().stream()
                .map(ThemeMapper::map)
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
