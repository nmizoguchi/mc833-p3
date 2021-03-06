/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import compute.*;
import model.Movie;
import java.util.*;
import java.util.Scanner;
import java.util.InputMismatchException;

public class ComputeClientInstance {

    Compute comp;
    Registry registry;

    public void run(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String name = "Compute";
            registry = LocateRegistry.getRegistry(args[0]);
            comp = (Compute) registry.lookup(name);

            executeClient();

        } catch (Exception e) {
            System.err.println("ComputeClient exception:");
            e.printStackTrace();
        }
    }

    void executeClient() throws RemoteException {
        while(true) {
            System.out.println("1) listar todos os títulos dos filmes e o ano de lançamento");
            System.out.println("2) listar todos os títulos dos filmes e o ano de lançamento de um gênero determinado");
            System.out.println("3) dado o identificador de um filme, retornar a sinopse do filme");
            System.out.println("4) dado o identificador de um filme, retornar todas as informações deste filme");
            System.out.println("5) listar todas as informações de todos os filmes");
            System.out.println("6) dado o identificador de um filme, alterar o número de exemplares em estoque");
            System.out.println("7) dado o identificador de um filme, retornar o número de exemplares em estoque");
            System.out.println("8) sair");

            // Le comando
            Scanner scanner = new Scanner(System.in);
            int cmd = 0;
            try{
                System.out.println("Digite o número da operação:");
                cmd = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Digite o número da operação:");
            }

            // Executa request conforme o comando
            switch(cmd) {
                case 1: {
                    MovieListResponse response = findMoviesWithTitleAndReleaseDate();
                    System.out.println(response);
                    break;
                }

                case 2: {
                    System.out.println("Digite o gênero:");
                    String genre = scanner.next();

                    MovieListResponse response = findMoviesWithTitleAndReleaseDateByGenre(genre);
                    System.out.println(response);
                    break;
                }

                case 3: {
                    System.out.println("Digite o id do filme:");
                    int id = scanner.nextInt();

                    MovieResponse response = findMovieSynopsis(id);
                    System.out.println(response);
                    break;
                }

                case 4: {
                    System.out.println("Digite o id do filme:");
                    int id = scanner.nextInt();

                    MovieResponse response = findMovie(id);
                    System.out.println(response);
                    break;
                }

                case 5: {
                    MovieListResponse response = findAllMovies();
                    System.out.println(response);
                    break;
                }

                case 6: {
                    System.out.println("Digite o id do filme:");
                    int id = scanner.nextInt();

                    RentMovieResponse response = rentMovie(id);
                    System.out.println(response);
                    break;
                }

                case 7: {
                    System.out.println("Digite o id do filme:");
                    int id = scanner.nextInt();

                    MovieResponse response = movieInStock(id);
                    System.out.println("Exemplares em estoque: "+response.movie.inStock);
                    break;
                }

                default: System.exit(0);
            }
        }
    }

    public MovieListResponse findMoviesWithTitleAndReleaseDate() throws RemoteException {
        List<String> projection = new ArrayList<String>();
        projection.add("title");
        projection.add("release_date");
        Request request = new MovieListRequest(projection);
        return (MovieListResponse)comp.executeRequest(request);
    }

    public MovieListResponse findMoviesWithTitleAndReleaseDateByGenre(String genre) throws RemoteException {
        List<String> projection = new ArrayList<String>();
        projection.add("title");
        projection.add("release_date");

        Request request = new MovieListByGenreRequest(genre, projection);
        return (MovieListResponse)comp.executeRequest(request);
    }

    public MovieResponse findMovieSynopsis(int id) throws RemoteException {
        List<String> projection = new ArrayList<String>();
        projection.add("synopsis");

        Request request = new MovieRequest(id, projection);
        return (MovieResponse)comp.executeRequest(request);
    }

    public MovieResponse findMovie(int id) throws RemoteException {
        List<String> projection = Movie.getFullProjection();

        Request request = new MovieRequest(id, projection);
        return (MovieResponse)comp.executeRequest(request);
    }

    public MovieListResponse findAllMovies() throws RemoteException {
        List<String> projection = Movie.getFullProjection();
        Request request = new MovieListRequest(projection);
        return (MovieListResponse)comp.executeRequest(request);
    }

    public RentMovieResponse rentMovie(int id) throws RemoteException {
        Request request = new RentMovieRequest(id);
        return (RentMovieResponse)comp.executeRequest(request);
    }

    public MovieResponse movieInStock(int id) throws RemoteException {
        List<String> projection = new ArrayList<String>();
        projection.add("inStock");

        Request request = new MovieRequest(id, projection);
       return (MovieResponse)comp.executeRequest(request);
    }
}
